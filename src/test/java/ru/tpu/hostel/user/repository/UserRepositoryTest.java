package ru.tpu.hostel.user.repository;

//@RepositoryTest
//@DisplayName("Тесты для репозитория пользователей")
@SuppressWarnings({"java:S125", "java:S2187", "unused"})
class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @BeforeEach
//    void setUp() {
//        userRepository.deleteAll();
//        roleRepository.deleteAll();
//
//        User adminUser1 = Data.getNewUser(
//                Data.FIRST_NAME_IVAN,
//                Data.LAST_NAME_IVANOV,
//                Data.EMAIL_IVANOV,
//                Data.PASSWORD_IVANOV,
//                Data.ROOM_NUMBER_101
//        );
//
//        User adminUser2 = Data.getNewUser(Data.FIRST_NAME_LEONID,
//                Data.LAST_NAME_LEONIDOV,
//                Data.EMAIL_LEONIDOV,
//                Data.PASSWORD_LEONIDOV,
//                Data.ROOM_NUMBER_102
//        );
//
//        User user1 = Data.getNewUser(
//                Data.FIRST_NAME_BOGDAN,
//                Data.LAST_NAME_BOGDANOV,
//                Data.EMAIL_BOGDANOV,
//                Data.PASSWORD_BOGDANOV,
//                Data.ROOM_NUMBER_103
//        );
//
//        User user2 = Data.getNewUser(
//                Data.FIRST_NAME_LEV,
//                Data.LAST_NAME_LEVY,
//                Data.EMAIL_LEVY,
//                Data.PASSWORD_LEVY,
//                Data.ROOM_NUMBER_104
//        );
//
//        userRepository.saveAll(List.of(adminUser1, adminUser2, user1, user2));
//
//        Role adminRole1 = new Role();
//        adminRole1.setRole(Roles.ADMINISTRATION);
//        adminRole1.setUser(adminUser1);
//
//        Role adminRole2 = new Role();
//        adminRole2.setRole(Roles.ADMINISTRATION);
//        adminRole2.setUser(adminUser2);
//
//        Role studentRole1 = new Role();
//        studentRole1.setRole(Roles.STUDENT);
//        studentRole1.setUser(user1);
//
//        Role studentRole2 = new Role();
//        studentRole2.setRole(Roles.STUDENT);
//        studentRole2.setUser(user2);
//
//        Role soopRole = new Role();
//        soopRole.setRole(Roles.RESPONSIBLE_SOOP);
//        soopRole.setUser(user1);
//
//        roleRepository.saveAll(List.of(adminRole1, adminRole2, studentRole1, studentRole2, soopRole));
//    }
//
//    @Test
//    @DisplayName("Поиск по роли в случае, когда есть несколько пользователей с такой ролью")
//    void findAllByRoleManyPersons() {
//            Pageable pageable = PageRequest.of(0, 10);
//            Page<User> result = userRepository.findAllByRoles_Role(Roles.ADMINISTRATION, pageable);
//
//            assertThat(result.getContent())
//                    .hasSize(2)
//                    .extracting(User::getEmail)
//                    .containsExactlyInAnyOrder(Data.EMAIL_IVANOV, Data.EMAIL_LEONIDOV);
//    }
//
//    @Test
//    @DisplayName("Поиск по роли в случае, когда есть один пользователь с такой ролью")
//    void findAllByRoleOnePerson() {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<User> result = userRepository.findAllByRoles_Role(Roles.RESPONSIBLE_SOOP, pageable);
//
//        assertThat(result.getContent())
//                .hasSize(1)
//                .extracting(User::getEmail)
//                .containsExactly(Data.EMAIL_BOGDANOV);
//    }
//
//    @Test
//    @DisplayName("Поиск по роли в случае, когда нет пользователей с такой ролью")
//    void findAllByRoleNoPersons() {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<User> result = userRepository.findAllByRoles_Role(Roles.RESPONSIBLE_KITCHEN, pageable);
//
//        assertThat(result.getContent())
//                .isEmpty();
//    }
//
}
