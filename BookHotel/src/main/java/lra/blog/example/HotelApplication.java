/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package lra.blog.example;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/lra")
public class HotelApplication extends Application {
    // Despite what it says in the helidon exmple, you don't seem to need to register
    // the LRA filter in the getClasses method. In fact it seems to break things if you do
}
