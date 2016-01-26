package iva.client.web.repositories;

import static iva.server.test.fixtures.Fixtures.newCategoryResponse;
import static iva.server.test.fixtures.Fixtures.username;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import iva.client.core.model.InterestModel;
import iva.client.core.model.User;
import iva.server.test.setup.TestDatabaseSetUp;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@IntegrationTest
public class InterestModelRestRepositoryTest extends TestDatabaseSetUp {

	private final InterestModelRestRepository restRepo = new InterestModelRestRepository();
	private URI testId;

	@Before
	public void setUp() throws Exception {
		testId = restRepo.findIdByOwnerUsername(username).get();
	}

	@Test
	public void testFindByOwnerUsername() {
		InterestModel model = restRepo.findByOwnerUsername(username).get();
		assertIsTestModel(model);
	}

	@Test
	public void testFindLinkedUser() {
		User user = restRepo.findLinkedUser(testId);
		assertEquals(testUser.getUsername(), user.getUsername());
	}

	@Test
	public void testSave() {
		InterestModel model = restRepo.findOne(testId);
		Map<String, Double> categories = model.getShortTermCategories();
		categories.putAll(newCategoryResponse());
		
		assertFalse(model.isNew());
		model = restRepo.save(model);
		assertFalse(model.isNew());
		
		assertEquals(categories, model.getShortTermCategories());
	}

	@Test
	public void testFindOne() {
		InterestModel model = restRepo.findOne(testId);
		assertIsTestModel(model);
	}

	@Test
	public void testExists() {
		assertTrue(restRepo.exists(testId));
		
		String idString = testId.toString();
		idString = idString.substring(0, idString.lastIndexOf("/"))+"/0";
		URI nonexistingId = URI.create(idString);
		assertFalse(restRepo.exists(nonexistingId));
	}

	@Test
	public void testFindAll() {
		List<InterestModel> models = restRepo.findAll();
		InterestModel model = models.get(0);
		assertIsTestModel(model);
	}

	@Test
	public void testCount() {
		assertTrue(restRepo.count() > 0);
	}

	@Test
	public void testDeleteId() {
		restRepo.delete(testId);
		assertFalse(restRepo.exists(testId));
	}

	@Test
	public void testDelete() {
		InterestModel model = restRepo.findOne(testId);
		restRepo.delete(model);
		assertFalse(restRepo.exists(testId));
	}

	@Test
	public void testDeleteAll() {
		restRepo.deleteAll();
		assertFalse(restRepo.exists(testId));
		assertEquals(0, restRepo.count());
	}

	private void assertIsTestModel(InterestModel model) {
		assertFalse(model.isNew());
		assertEquals(testModel.getProperties(), model.getProperties());
		assertEquals(testModel.getShortTermCategories(), model.getShortTermCategories());
		assertEquals(testModel.getLongTermCategories(), model.getLongTermCategories());
	}

}
