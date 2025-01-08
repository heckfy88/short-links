package sf.shortlinks;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultMatcher;
import sf.shortlinks.api.dto.CreateLinkRqDto;
import sf.shortlinks.api.dto.CreateLinkRsDto;
import sf.shortlinks.api.dto.EditLinkRqDto;
import sf.shortlinks.api.dto.RedirectLinkRqDto;
import sf.shortlinks.repository.LinkRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Sql(scripts = "/sql/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "delete from link", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LinkControllerIT extends AbstractIntegrationClass {

    @Autowired
    LinkRepository linkRepository;

    static {
        postgresContainer.start();
    }

    @Test
    @DisplayName("Один пользователь может создавать несколько коротких ссылок на разные ресурсы.")
    public void createLink_success() throws Exception {
        CreateLinkRqDto requestDto1 = new CreateLinkRqDto(
                "google.com",
                null,
                120,
                6
        );
        String response = create(requestDto1, status().isOk());
        CreateLinkRsDto rsDto = mapper.readValue(response, CreateLinkRsDto.class);

        CreateLinkRqDto requestDto2 = new CreateLinkRqDto(
                "yahoo.com",
                rsDto.ownerUid(),
                120,
                6
        );
        create(requestDto2, status().isOk());

        assertEquals(2, linkRepository.countLinksByOwnerUid(rsDto.ownerUid()));
    }

    @Test
    @DisplayName("Успешное изменение короткой ссылки при дублировании запроса на создание")
    public void createLink_repeat_success() throws Exception {
        CreateLinkRqDto requestDto = new CreateLinkRqDto(
                "https://example.com/1",
                UUID.fromString("7b0f45b4-7ca8-432b-8e23-3554f740eeff"),
                null,
                null
        );
        String response = create(requestDto, status().isOk());
        CreateLinkRsDto rsDto = mapper.readValue(response, CreateLinkRsDto.class);

        assertNotEquals("short1", rsDto.shortUrl());
    }

    @Test
    @DisplayName("Успешное создание разных ссылок для разных пользователей")
    public void createLink_multipleUsers_success() throws Exception {
        CreateLinkRqDto requestDto1 = new CreateLinkRqDto(
                "https://example.com/999",
                null,
                null,
                null
        );
        String response1 = create(requestDto1, status().isOk());


        CreateLinkRqDto requestDto2 = new CreateLinkRqDto(
                "https://example.com/999",
                null,
                null,
                null
        );
        String response2 = create(requestDto2, status().isOk());

        CreateLinkRsDto rsDto1 = mapper.readValue(response1, CreateLinkRsDto.class);
        CreateLinkRsDto rsDto2 = mapper.readValue(response2, CreateLinkRsDto.class);

        assertNotEquals(rsDto1.shortUrl(), rsDto2.shortUrl());
    }

    @Test
    @DisplayName("Успешное обновление короткой ссылки")
    public void updateLink_success() throws Exception {
        CreateLinkRqDto createDto = new CreateLinkRqDto(
                "https://example.com/999",
                null,
                120,
                60
        );
        String response = create(createDto, status().isOk());
        CreateLinkRsDto rsDto = mapper.readValue(response, CreateLinkRsDto.class);

        EditLinkRqDto editDto = new EditLinkRqDto(
                rsDto.ownerUid(),
                rsDto.shortUrl(),
                100,
                1000,
                false
        );

        edit(editDto, status().isOk());
    }

    @Test
    @DisplayName("Успешный редирект по короткой ссылки")
    public void redirect_success() throws Exception {
        RedirectLinkRqDto dto = new RedirectLinkRqDto(
                UUID.fromString("7b0f45b4-7ca8-432b-8e23-3554f740eeff"),
                "www.short1.ru/1"
        );

        String url = redirect(dto, status().is3xxRedirection());

        assertEquals("https://example.com/1", url);

    }

    private String create(CreateLinkRqDto dto, ResultMatcher expectedStatus) throws Exception {
        return mvc.perform(post("/api/v1/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                ).andExpect(expectedStatus)
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();
    }

    private String edit(EditLinkRqDto dto, ResultMatcher expectedStatus) throws Exception {
        return mvc.perform(patch("/api/v1/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                ).andExpect(expectedStatus)
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();
    }

    private String redirect(RedirectLinkRqDto dto, ResultMatcher expectedStatus) throws Exception {
        return mvc.perform(post("/api/v1/link/redirect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                ).andExpect(expectedStatus)
                .andDo(print())
                .andReturn()
                .getResponse().getHeader("Location");
    }
}
