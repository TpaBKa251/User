package ru.tpu.hostel.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.tpu.hostel.internal.utils.ExecutionContext;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.dto.request.LinkType;
import ru.tpu.hostel.user.dto.response.ContactResponseDto;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.repository.ContactRepository;
import ru.tpu.hostel.user.repository.UserRepository;
import ru.tpu.hostel.user.service.impl.ContactServiceImpl;
import ru.tpu.hostel.user.service.impl.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.tpu.hostel.internal.exception.ServiceException.Conflict;
import static ru.tpu.hostel.internal.exception.ServiceException.Forbidden;
import static ru.tpu.hostel.internal.exception.ServiceException.NotFound;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private RoleService roleService;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    @TempDir
    Path tempDir;

    @Test
    void addContactWithSuccess() {
        ReflectionTestUtils.setField(contactService, "uploadDir", tempDir.toString());
        MultipartFile photoFile = new MockMultipartFile(
                "photoFile", "photo.png", "image/png", "image".getBytes(StandardCharsets.UTF_8));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            contact.setId(TestData.CONTACT_ID);
            return contact;
        });

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.HOSTEL_SUPERVISOR));

            ContactResponseDto result = contactService.addContact(photoFile, TestData.contactAddRequestDto());

            assertThat(result.id()).isEqualTo(TestData.CONTACT_ID);
            verify(contactRepository).save(any(Contact.class));
        }
    }

    @Test
    void addContactWhenNoPermission() {
        MultipartFile photoFile = new MockMultipartFile(
                "photoFile", "photo.png", "image/png", "image".getBytes(StandardCharsets.UTF_8));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.STUDENT));

            assertThatThrownBy(() -> contactService.addContact(photoFile, TestData.contactAddRequestDto()))
                    .isInstanceOf(Forbidden.class);
        }
    }

    @Test
    void addContactWhenFileIsEmpty() {
        MultipartFile photoFile = new MockMultipartFile(
                "photoFile", "photo.png", "image/png", new byte[0]);

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.HOSTEL_SUPERVISOR));

            assertThatThrownBy(() -> contactService.addContact(photoFile, TestData.contactAddRequestDto()))
                    .isInstanceOf(NotFound.class);
        }
    }

    @Test
    void getAllCustomContactsWithSuccess() {
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);
        when(contactRepository.getAllCustomContacts()).thenReturn(List.of(contact));

        List<ContactResponseDto> result = contactService.getAllCustomContacts();

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUsersWithRoleContactsWithSuccess() {
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);
        when(contactRepository.getAllMainContacts(anyList())).thenReturn(List.of(contact));

        List<ContactResponseDto> result = contactService.getAllUsersWithRoleContacts();

        assertThat(result).hasSize(1);
    }

    @Test
    void deleteContactWithSuccess() {
        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.HOSTEL_SUPERVISOR));

            contactService.deleteContact(TestData.CONTACT_ID);

            verify(contactRepository).deleteById(TestData.CONTACT_ID);
        }
    }

    @Test
    void deleteContactWhenNoPermission() {
        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.STUDENT));

            assertThatThrownBy(() -> contactService.deleteContact(TestData.CONTACT_ID))
                    .isInstanceOf(Forbidden.class);
        }
    }

    @Test
    void addLinkWithSuccess() {
        User user = TestData.newUser(
                TestData.OTHER_USER_ID, TestData.FIRST_NAME_BOGDAN, TestData.LAST_NAME_BOGDANOV,
                TestData.EMAIL_BOGDANOV, TestData.ROOM_NUMBER_102);
        when(userRepository.findById(TestData.OTHER_USER_ID)).thenReturn(Optional.of(user));
        when(roleService.getAllUserRoles(TestData.OTHER_USER_ID)).thenReturn(List.of(Roles.STUDENT.name()));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.HOSTEL_SUPERVISOR));

            contactService.addLink(TestData.addLinkRequestDto(LinkType.TG, TestData.TG_LINK_WITH_AT, TestData.OTHER_USER_ID));

            verify(contactRepository).saveAll(anyList());
            verify(contactRepository).flush();
        }
    }

    @Test
    void addLinkWhenForbidden() {
        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.STUDENT));

            assertThatThrownBy(() -> contactService.addLink(
                    TestData.addLinkRequestDto(LinkType.TG, TestData.TG_LINK_WITH_AT, TestData.OTHER_USER_ID)))
                    .isInstanceOf(Forbidden.class);
        }
    }

    @Test
    void editLinkWithSuccess() {
        User user = TestData.defaultUser();
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));
        when(contactRepository.findAllByEmail(user.getEmail())).thenReturn(List.of(contact));

        contactService.editLink(TestData.addLinkRequestDto(LinkType.TG, TestData.TG_LINK_WITH_AT, TestData.USER_ID));

        verify(contactRepository).updateTgLink(TestData.CONTACT_ID, TestData.TG_LINK);
        verify(contactRepository).flush();
    }

    @Test
    void editLinkWhenConflict() {
        User user = TestData.defaultUser();
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));
        when(contactRepository.findAllByEmail(user.getEmail())).thenReturn(List.of(contact));
        doThrow(new DataIntegrityViolationException("duplicate")).when(contactRepository).flush();

        assertThatThrownBy(() -> contactService.editLink(
                TestData.addLinkRequestDto(LinkType.VK, TestData.VK_LINK_FULL, TestData.USER_ID)))
                .isInstanceOf(Conflict.class);
    }

    @Test
    void editLinkWhenUserNotFound() {
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.editLink(
                TestData.addLinkRequestDto(LinkType.TG, TestData.TG_LINK_WITH_AT, TestData.USER_ID)))
                .isInstanceOf(NotFound.class);
    }
}
