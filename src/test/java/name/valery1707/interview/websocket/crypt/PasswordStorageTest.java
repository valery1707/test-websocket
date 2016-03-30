package name.valery1707.interview.websocket.crypt;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PasswordStorageTest {

	public static final String CORRECT_HASH_1 = "sha1:64000:18:5K4fWttzIlBkBtZLUo7cLJpbptljrKNz:X/Pn4pEFuZ5hTppkKPgtrZLR";
	public static final String CORRECT_HASH_2 = "sha1:64000:18:GTw+BgAw9dmparfX+9/Xp9Vw57ged07S:+8bLkHOoMYPjZ9dwtq1AqLXN";
	public static final String CORRECT_HASH_3 = "sha1:64000:10:4G79SdJZcJ54UK2q2LZorwDTlWBUzrnX:DzJSf2VqIuOtNw==";

	@Test
	public void testGood() throws Exception {
		String password = "test_password";
		String hash = PasswordStorage.createHash(password);
		System.out.println("hash = " + hash);
		assertTrue(PasswordStorage.verifyPassword(password, hash));
		assertTrue(PasswordStorage.verifyPassword(password, CORRECT_HASH_1));
		assertTrue(PasswordStorage.verifyPassword(password, CORRECT_HASH_2));
		assertTrue(PasswordStorage.verifyPassword(password, CORRECT_HASH_3));
	}

	@Test
	public void testBadPassword() throws Exception {
		String password = "test_password!";
		String hash = PasswordStorage.createHash(password);
		assertFalse(PasswordStorage.verifyPassword("wrongPassword", hash));
	}

	@Test(expected = PasswordStorage.InvalidHashException.class)
	public void testHashFormat_1() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", "wrongHash");
	}

	@Test(expected = PasswordStorage.CannotPerformOperationException.class)
	public void testHashFormat_2() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", CORRECT_HASH_1.replace("sha1:", "sha2:"));
	}

	@Test(expected = PasswordStorage.InvalidHashException.class)
	public void testHashFormat_3() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", CORRECT_HASH_1.replace(":64000:", ":number?:"));
	}

	@Test(expected = PasswordStorage.InvalidHashException.class)
	public void testHashFormat_4() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", CORRECT_HASH_1.replace(":64000:", ":-5:"));
	}

	@Test(expected = PasswordStorage.InvalidHashException.class)
	public void testHashFormat_5() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", CORRECT_HASH_1.replace(":64000:", ":0:"));
	}

	@Test(expected = PasswordStorage.InvalidHashException.class)
	@Ignore
	public void testHashFormat_6() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", CORRECT_HASH_1.replace(CORRECT_HASH_1.split(":")[PasswordStorage.SALT_INDEX], "±±±"));
	}

	@Test(expected = PasswordStorage.InvalidHashException.class)
	@Ignore
	public void testHashFormat_7() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", CORRECT_HASH_1.replace(CORRECT_HASH_1.split(":")[PasswordStorage.HASH_INDEX], "±±±"));
	}

	@Test(expected = PasswordStorage.InvalidHashException.class)
	public void testHashFormat_8() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", CORRECT_HASH_1.replace(":18:", ":number?:"));
	}

	@Test(expected = PasswordStorage.InvalidHashException.class)
	public void testHashFormat_9() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", CORRECT_HASH_1.replace(":18:", ":-5:"));
	}

	@Test(expected = PasswordStorage.InvalidHashException.class)
	public void testHashFormat_10() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", CORRECT_HASH_1.replace(":18:", ":0:"));
	}

	@Test(expected = PasswordStorage.InvalidHashException.class)
	public void testHashFormat_11() throws Exception {
		PasswordStorage.verifyPassword("wrongPassword", CORRECT_HASH_1.replace(CORRECT_HASH_1.split(":")[PasswordStorage.HASH_INDEX], CORRECT_HASH_1.split(":")[PasswordStorage.HASH_INDEX].substring(5)));
	}
}
