package apacheHttpClient.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToUpdate {
    private User userNewValues;
    private User userToChange;
}
