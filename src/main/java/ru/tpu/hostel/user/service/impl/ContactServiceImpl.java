package ru.tpu.hostel.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.internal.exception.ServiceException;
import ru.tpu.hostel.internal.utils.ExecutionContext;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.dto.request.AddLinkRequestDto;
import ru.tpu.hostel.user.dto.request.ContactAddRequestDto;
import ru.tpu.hostel.user.dto.response.ContactResponseDto;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.mapper.ContactMapper;
import ru.tpu.hostel.user.repository.ContactRepository;
import ru.tpu.hostel.user.repository.UserRepository;
import ru.tpu.hostel.user.service.ContactService;
import ru.tpu.hostel.user.service.RoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.tpu.hostel.user.util.MessageConctants.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private static final String CONFLICT_VERSIONS_EXCEPTION_MESSAGE
            = "Кто-то уже изменил профиль. Обновите данные и повторите попытку";

    private static final String ADDING_LINK_FORBIDDEN_EXCEPTION_MESSAGE
            = "У вас нет прав изменять контакты жителей другого этажа";

    private static final List<String> STARTS_OF_LINKS = List.of("https:", "http:", "t.me", "vk.com");

    private static final String START_OF_SOCIAL_NAME = "@";

    private final UserServiceImpl userService;

    private final RoleService roleService;

    private final ContactRepository contactRepository;

    private final UserRepository userRepository;

    @Override
    public ContactResponseDto addContact(ContactAddRequestDto contactAddRequestDto) {
        Contact contact = ContactMapper.mapToContact(contactAddRequestDto);

        Contact savedContact = contactRepository.save(contact);

        return ContactMapper.mapToContactResponseDto(savedContact);
    }

    @Override
    public List<ContactResponseDto> getAllContacts() {
        List<String> excludedRoles = getAllExcludedRoles();

        List<Contact> contacts = contactRepository.getAllMainContacts(excludedRoles);

        return contacts.stream()
                .map(ContactMapper::mapToContactResponseDto)
                .toList();
    }

    @Override
    public void deleteContact(UUID id) {
        contactRepository.deleteById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addLink(AddLinkRequestDto addLinkRequestDto) {
        User user = getUserForLinkAddition(addLinkRequestDto.userId());
        List<Roles> userRoles = roleService.getAllUserRoles(addLinkRequestDto.userId())
                .stream()
                .map(Roles::valueOf)
                .toList();

        String socialMediaSiteName = extractUsernameFromLink(addLinkRequestDto.link());

        List<Contact> contacts = ContactMapper.createContacts(
                user,
                userRoles,
                socialMediaSiteName,
                addLinkRequestDto.linkType()
        );

        contactRepository.saveAll(contacts);
    }

    private User getUserForLinkAddition(UUID userId) {
        ExecutionContext context = ExecutionContext.get();

        if (userId == null) {
            return getUserByI(context.getUserID());
        }

        if (context.getUserRoles().contains(Roles.HOSTEL_SUPERVISOR)) {
            return getUserByI(userId);
        }

        if (context.getUserRoles().contains(Roles.FLOOR_SUPERVISOR)) {
            String currentUserRoomNumber = userService.getRoomNumberByUserId(context.getUserID());
            String userForLinkAdditionalRoomNumber = userService.getRoomNumberByUserId(userId);

            if (currentUserRoomNumber.charAt(0) == userForLinkAdditionalRoomNumber.charAt(0)) {
                return getUserByI(userId);
            }
        }

        throw new ServiceException.Forbidden(ADDING_LINK_FORBIDDEN_EXCEPTION_MESSAGE);
    }

    private User getUserByI(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException.NotFound(USER_NOT_FOUND_MESSAGE));
    }

    private String extractUsernameFromLink(String link) {
        if (link.startsWith(START_OF_SOCIAL_NAME)) {
            return link.substring(1);
        }
        if (STARTS_OF_LINKS.stream().anyMatch(link::startsWith)) {
            return link.substring(link.lastIndexOf('/') + 1);
        }

        return link;
    }

    private List<String> getAllExcludedRoles() {
        List<String> excludedRoles = new ArrayList<>();
        excludedRoles.add(Roles.WORKER_GYM.toString());
        excludedRoles.add(Roles.WORKER_FIRE_SAFETY.toString());
        excludedRoles.add(Roles.WORKER_SANITARY.toString());
        excludedRoles.add(Roles.WORKER_SOOP.toString());
        excludedRoles.add(Roles.STUDENT.toString());

        return excludedRoles;
    }

//    private boolean isStudentOrWorker(String role) {
//        if
//    }
}
