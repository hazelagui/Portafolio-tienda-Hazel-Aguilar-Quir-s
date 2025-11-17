package Tienda.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.google.cloud.storage.Storage;

@SpringBootTest
class TiendaHazelApplicationTests {
    
        @MockBean
        private Storage storage;
   
	@Test
	void contextLoads() {
	}

}
