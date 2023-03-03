package pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private int age;
    private String name;
    private String sex;
    private String zipCode;

    public static User random(String zipcode) {
        return new User(RandomUtils.nextInt(0, 100),
                RandomStringUtils.randomAlphabetic(10), "FEMALE", zipcode);
    }

    public static User random() {
        return random(String.valueOf(RandomUtils.nextInt(10000, 20000)));
    }
}
