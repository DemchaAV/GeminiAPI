import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Delegate;

import java.util.ArrayList;
@Value
@RequiredArgsConstructor
public class TestLombok<T> {
 @Delegate @Getter(AccessLevel.NONE)
    ArrayList <T> list ;
 int age;
 String name;
}
class testmainLombok{
    public static void main(String[] args) {
        TestLombok<String> list = new TestLombok<>(new ArrayList<>(),15,"Anatoliy");

        list.add("hello");
      
    }
}
