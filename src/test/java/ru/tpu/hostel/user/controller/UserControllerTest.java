package ru.tpu.hostel.user.controller;

//@SpringBootTest
//@AutoConfigureMockMvc
//@DisplayName("Интеграционные тесты контроллера сессии SessionController")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ExtendWith(PostgresTestContainerExtension.class)
@SuppressWarnings({"java:S125", "java:S2187", "unused"})
class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private UserServiceImpl userService;
//
//    private List<UserShortResponseDto2> adminUsers;
//
//    @BeforeEach
//    void setUp() {
//        adminUsers = List.of(
//                new UserShortResponseDto2(
//                        UUID.randomUUID(),
//                        Data.FIRST_NAME_IVAN,
//                        Data.LAST_NAME_IVANOV,
//                        null, "", ""),
//                new UserShortResponseDto2(
//                        UUID.randomUUID(),
//                        Data.FIRST_NAME_LEONID,
//                        Data.LAST_NAME_LEONIDOV,
//                        null, "", "")
//        );
//    }
//
//    @AfterEach
//    void commonVerify() {
//        verifyNoMoreInteractions(userService);
//    }
//
//    @Test
//    @DisplayName("Получение списка пользователей по роли")
//    void testGetUsersByRole_Success() throws Exception {
//        Roles role = Roles.ADMINISTRATION;
//        int page = 0;
//        int size = 10;
//
//        when(userService.getAllUsersByRole(role, page, size, false)).thenReturn(adminUsers);
//
//        mockMvc.perform(get("/users/get/by/role")
//                        .param("role", role.name())
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(adminUsers.size()))
//                .andExpect(jsonPath("$[0].id").value(adminUsers.get(0).id().toString()))
//                .andExpect(jsonPath("$[0].firstName").value(adminUsers.get(0).firstName()))
//                .andExpect(jsonPath("$[0].lastName").value(adminUsers.get(0).lastName()))
//                .andExpect(jsonPath("$[0].middleName").value(adminUsers.get(0).middleName()))
//                .andExpect(jsonPath("$[1].id").value(adminUsers.get(1).id().toString()))
//                .andExpect(jsonPath("$[1].firstName").value(adminUsers.get(1).firstName()))
//                .andExpect(jsonPath("$[1].lastName").value(adminUsers.get(1).lastName()))
//                .andExpect(jsonPath("$[1].middleName").value(adminUsers.get(1).middleName()));
//
//        verify(userService, times(1)).getAllUsersByRole(role, page, size, false);
//    }
//
//    @Test
//    @DisplayName("Получение списка пользователей по роли (пустой список)")
//    void testGetUsersByRole_EmptyList() throws Exception {
//        Roles role = Roles.STUDENT;
//        int page = 0;
//        int size = 10;
//
//        when(userService.getAllUsersByRole(role, page, size, false)).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(get("/users/get/by/role")
//                        .param("role", role.name())
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(0));
//
//        verify(userService, times(1)).getAllUsersByRole(role, page, size, false);
//    }
}
