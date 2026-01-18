package ru.tpu.hostel.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

import static ru.tpu.hostel.user.dto.request.LinkType.TG;
import static ru.tpu.hostel.user.util.CommonMessages.USER_NOT_FOUND_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    public static final String SOMEONE_CHANGE_CONTACTS_TRY_LATER = "Кто-то уже изменил ссылки. Обновите данные и повторите попытку";
    private static final String ADDING_CONTACT_FORBIDDEN_EXCEPTION_MESSAGE
            = "У вас нет прав изменять карточки контактов";

    private static final String ADDING_LINK_FORBIDDEN_EXCEPTION_MESSAGE
            = "У вас нет прав изменять контакты жителей другого этажа";

    private static final String USER_ALREADY_HAS_LINKS_EXCEPTION_MESSAGE = "Пользователь уже имеет эти ссылки";

    private static final List<String> STARTS_OF_LINKS = List.of("https:", "http:", "t.me", "vk.com");

    private static final String START_OF_SOCIAL_NAME = "@";

    private final UserServiceImpl userService;

    private final RoleService roleService;

    private final ContactRepository contactRepository;

    private final UserRepository userRepository;

    @Override
    public ContactResponseDto addContact(ContactAddRequestDto contactAddRequestDto) {
        ExecutionContext context = ExecutionContext.get();

        if (context.getUserRoles().contains(Roles.HOSTEL_SUPERVISOR)) {
            Contact contact = ContactMapper.mapToContact(contactAddRequestDto);

            Contact savedContact = contactRepository.save(contact);

            return ContactMapper.mapToContactResponseDto(savedContact);
        }

        throw new ServiceException.Forbidden(ADDING_CONTACT_FORBIDDEN_EXCEPTION_MESSAGE);
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
        ExecutionContext context = ExecutionContext.get();

        if (context.getUserRoles().contains(Roles.HOSTEL_SUPERVISOR)) {
            contactRepository.deleteById(id);
            return;
        }

        throw new ServiceException.Forbidden(ADDING_CONTACT_FORBIDDEN_EXCEPTION_MESSAGE);
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
        contactRepository.flush();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void editLink(AddLinkRequestDto addLinkRequestDto) {
        User user = getUserByI(addLinkRequestDto.userId());

        String socialMediaSiteName = extractUsernameFromLink(addLinkRequestDto.link());

        contactRepository.findAllByEmail(user.getEmail())
                .forEach(contact -> {
                    try {
                        if (addLinkRequestDto.linkType() == TG) {
                            contactRepository.updateTgLink(contact.getId(), socialMediaSiteName);
                        } else {
                            contactRepository.updateVkLink(contact.getId(), socialMediaSiteName);
                        }

                        contactRepository.flush();
                    } catch (DataIntegrityViolationException e) {
                        throw new ServiceException.Conflict(USER_ALREADY_HAS_LINKS_EXCEPTION_MESSAGE);
                    } catch (ObjectOptimisticLockingFailureException e) {
                        throw new ServiceException.Conflict(SOMEONE_CHANGE_CONTACTS_TRY_LATER);
                    }
                });
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

}
