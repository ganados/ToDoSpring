package task.todo.springwebapp;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import task.todo.springwebapp.service.ToDoService;
import task.todo.springwebapp.web.controllers.UserController;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
class SpringwebappApplicationTests {

	private static final String APP_PATH = "http://localhost:8080/todo";
	private static final String USER_PATH = "/user";

	private static final Gson gson = new Gson();

	@MockBean
	ToDoService toDoService;

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
				.perform(MockMvcRequestBuilders
				.post(APP_PATH + USER_PATH)
				.content(string)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
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
				.perform(MockMvcRequestBuilders
						.post(APP_PATH + USER_PATH)
						.content(string)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Timeout(1)
	@Test
	public void shouldReturnConflictCode_whileDoubleUser() throws Exception {
		HashMap<String, String> names = new HashMap<>();
		names.put("username", "janKowalski");
		names.put("password", "abcd213");

		String string = gson.toJson(names);

		this.mockMvc
				.perform(MockMvcRequestBuilders
						.post(APP_PATH + USER_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(string));
		this.mockMvc
				.perform(MockMvcRequestBuilders
						.post(APP_PATH + USER_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(string))
				.andExpect(status().isConflict());
	}

}
