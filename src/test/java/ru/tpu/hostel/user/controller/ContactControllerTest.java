package ru.tpu.hostel.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.dto.request.LinkType;
import ru.tpu.hostel.user.dto.response.ContactResponseDto;
import ru.tpu.hostel.user.service.ContactService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContactService contactService;

    private ContactResponseDto contactResponseDto() {
        return new ContactResponseDto(
                TestData.CONTACT_ID, "Иванов Иван", "Студент", TestData.EMAIL_IVANOV,
                "/users/images/photo.png", TestData.TG_LINK, TestData.VK_LINK);
    }

    @Test
    void addContactWithSuccess() throws Exception {
        when(contactService.addContact(any(), any())).thenReturn(contactResponseDto());
        MockMultipartFile photo = new MockMultipartFile(
                "photoFile", "photo.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile contact = new MockMultipartFile(
                "contact", "contact", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(TestData.contactAddRequestDto()));

        mockMvc.perform(multipart("/contacts").file(photo).file(contact))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestData.CONTACT_ID.toString()));
    }

    @Test
    void getAllCustomContactsWithSuccess() throws Exception {
        when(contactService.getAllCustomContacts()).thenReturn(List.of(contactResponseDto()));

        mockMvc.perform(get("/contacts/custom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllUsersWithRoleContactsWithSuccess() throws Exception {
        when(contactService.getAllUsersWithRoleContacts()).thenReturn(List.of(contactResponseDto()));

        mockMvc.perform(get("/contacts/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteContactWithSuccess() throws Exception {
        mockMvc.perform(delete("/contacts/{contactId}", TestData.CONTACT_ID))
                .andExpect(status().isOk());
    }

    @Test
    void addLinkWithSuccess() throws Exception {
        mockMvc.perform(post("/contacts/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestData.addLinkRequestDto(LinkType.TG, TestData.TG_LINK_WITH_AT, TestData.USER_ID))))
                .andExpect(status().isNoContent());
    }
}
