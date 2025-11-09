package ru.tpu.hostel.user.service;

//@ExtendWith(MockitoExtension.class)
//@DisplayName("Тесты сервиса пользователей UserServiceImpl")
@SuppressWarnings({"java:S125", "java:S2187", "unused"})
class UserServiceImplTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private List<User> adminUsers;
//
//    private Pageable pageable;
//
//    @BeforeEach
//    void setUp() {
//        pageable = PageRequest.of(0, 10, Sort.by("id"));
//
//        User adminUser1 = Data.getNewUser(
//                Data.FIRST_NAME_IVAN,
//                Data.LAST_NAME_IVANOV,
//                Data.EMAIL_IVANOV,
//                Data.PASSWORD_IVANOV,
//                Data.ROOM_NUMBER_101
//        );
//        User adminUser2 = Data.getNewUser(
//                Data.FIRST_NAME_LEONID,
//                Data.LAST_NAME_LEONIDOV,
//                Data.EMAIL_LEONIDOV,
//                Data.PASSWORD_LEONIDOV,
//                Data.ROOM_NUMBER_101
//        );
//
//        adminUsers = List.of(adminUser1, adminUser2);
//    }
//
//    @AfterEach
//    void commonVerify() {
//        verifyNoMoreInteractions(userRepository);
//    }
//
//    @Test
//    @DisplayName("Тест получения всех пользователей по роли")
//    void testGetAllUsersByRole_AdminRole() {
//        Roles role = Roles.ADMINISTRATION;
//        Page<User> mockPage = new PageImpl<>(adminUsers);
//
//        when(userRepository.findAllByRoles_Role(role, pageable)).thenReturn(mockPage);
//
//        List<UserShortResponseDto2> result = userService.getAllUsersByRole(role, 0, 10, false);
//
//        assertThat(result)
//                .hasSize(2)
//                .extracting(UserShortResponseDto2::id, UserShortResponseDto2::firstName, UserShortResponseDto2::lastName, UserShortResponseDto2::middleName)
//                .containsExactlyInAnyOrder(
//                        tuple(
//                                adminUsers.get(0).getId(),
//                                adminUsers.get(0).getFirstName(),
//                                adminUsers.get(0).getLastName(),
//                                adminUsers.get(0).getMiddleName()
//                        ),
//                        tuple(
//                                adminUsers.get(1).getId(),
//                                adminUsers.get(1).getFirstName(),
//                                adminUsers.get(1).getLastName(),
//                                adminUsers.get(1).getMiddleName()
//                        )
//                );
//
//        verify(userRepository, times(1)).findAllByRoles_Role(role, pageable);
//    }
//
//    @Test
//    @DisplayName("Тест получения всех пользователей по роли, когда нет результатов")
//    void testGetAllUsersByRole_EmptyResult() {
//        Roles role = Roles.RESPONSIBLE_KITCHEN;
//
//        when(userRepository.findAllByRoles_Role(role, pageable)).thenReturn(Page.empty());
//
//        List<UserShortResponseDto2> result = userService.getAllUsersByRole(role, 0, 10, false);
//
//        assertThat(result).isEmpty();
//        verify(userRepository, times(1)).findAllByRoles_Role(role, pageable);
//    }
}
