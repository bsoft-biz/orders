package biz.bsoft;

import biz.bsoft.orders.dao.ItemRepository;
import biz.bsoft.orders.model.Item;
import biz.bsoft.users.dao.UserRepository;
import biz.bsoft.users.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class OrdersApplicationTests extends AbstractTransactionalJUnit4SpringContextTests {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	private static final Logger logger =
			LoggerFactory.getLogger(OrdersApplicationTests.class);
	private String email="test@gmail.com";
	private String userName="testUser";
	private String password=email;
	@Before
	public void givenUserAndVerificationToken() {
		User user = new User();
		user.setUsername(userName);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setEnabled(true);
		entityManager.persist(user);

		entityManager.flush();
		entityManager.clear();
//		TestTransaction.flagForCommit();
//		TestTransaction.end();
	}

	@After
	public void flushAfter() {
		entityManager.flush();
		entityManager.clear();
	}
	//@Test
	public void testRestItemsGetCount() {
		//get test user from properties file or create one in test
		TestRestTemplate restTemplateWithAuth = restTemplate.withBasicAuth(userName,password);
		HttpHeaders requestHeaders = new HttpHeaders();
		HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
		String url;
		url= "/users/user";
		ResponseEntity<String> userResponse =
				restTemplateWithAuth.exchange(url, HttpMethod.GET, requestEntity, String.class);
		String user = userResponse.getBody();
		logger.info(user);
		assertThat("Status code is ok",userResponse.getStatusCode(),equalTo(HttpStatus.OK));
		//--
		url= "/orders/items";
		ResponseEntity<List<Item>> itemResponse =
				restTemplateWithAuth.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<Item>>() {});
		List<Item> items = itemResponse.getBody();
		//logger.info(items.toString());
		assertEquals(items.size(),itemRepository.count());
	}
	@Test
	public void testUserRepositoryCanFindUser(){
		User user=userRepository.findByEmail(email);
		assertNotNull(user);
		//logger.info(user.toString());
		assertThat("Can decode password",passwordEncoder.matches(password,user.getPassword()));
		assertEquals(userName, user.getUsername());
		assertEquals(email, user.getEmail());
	}
}
