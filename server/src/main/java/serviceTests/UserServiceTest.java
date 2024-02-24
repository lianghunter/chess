package serviceTests;

import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {
    private final UserService service = new UserService();
    @Test
    void clear() {
//        service.addPet(new Pet(0, "joe", PetType.FISH));
//        service.addPet(new Pet(0, "sally", PetType.CAT));
//        service.addPet(new Pet(0, "fido", PetType.DOG));

        service.clear();
        assertEquals(0, service.listAuth().size());
    }
}
