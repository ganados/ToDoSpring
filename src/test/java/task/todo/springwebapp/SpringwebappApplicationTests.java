package task.todo.springwebapp;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import task.todo.springwebapp.service.ToDoService;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
class SpringwebappApplicationTests {

	private static final String APP_PATH = "http://localhost:8080/todo";
	private static final String USER_PATH = "/user";

	private static final Gson gson = new Gson();

	@Autowired
	private MockMvc mockMvc;

	@Timeout(1)
	@Test
	public void shouldReturnOkCode_whileUserCreated() throws Exception {
		HashMap<String, String> names = new HashMap<>();
		names.put("username", "janKowalski");
		names.put("password", "abcd213");

		String string = gson.toJson(names);

		this.mockMvc
				.perform(post(APP_PATH + USER_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(string))
				.andExpect(status().isOk());
	}

	@Timeout(1)
	@CsvSource(value = {"JanKowalski,null","null,am!aK@42",
			"null,null","'  ',@!asd#41",
			"janKowalski,'   '"}, nullValues={"null"})
	@ParameterizedTest(name = "[{index}] username = {0}, password = {1}")
	public void shouldReturnBadRequestCode_whileMissingData(String username, String password) throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put("username", username);
		map.put("password", password);

		String string = gson.toJson(map);

		this.mockMvc
				.perform(post(APP_PATH + USER_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(string))
				.andExpect(status().isBadRequest());
	}

//	@Timeout(1)
//	@Test
//	public void shouldReturnConflictCode_whileDoubleUser() throws Exception {
//		HashMap<String, String> names = new HashMap<>();
//		names.put("username", "janKowalski");
//		names.put("password", "abcd213");
//
//		String string = gson.toJson(names);
//
//		this.mockMvc
//				.perform(post(APP_PATH + USER_PATH)
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(string));
//		this.mockMvc
//				.perform(post(APP_PATH + USER_PATH)
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(string))
//				.andExpect(status().isConflict());
//	}

}
