package locatorservice

import com.jaeckel.locator.User
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class UserController {

  def index = {

    render("HELO")


  }

  def create = {

    if (request.isSecure()) {

      User u = new User()

      u.name = params.name
      u.email = params.email
      u.pubKeyId = params.pubKeyId
      u.pubKey = params.pubKey
      String password = params.password
      u.passwordHash = generateValue(password)


      if (u.validate()) {
        u.save();
        render("user: " + u + " created.")
        return
      }

      render("user not valid.")


    } else {

      render("no SSL")

    }

  }

  def list = {

    List<User> users = User.list()

    def response = "{\n";

    users.each {User u ->
      response += "{ "
      response += "id: " + u.id + ", "
      response += "name: " + u.name + ", "
      response += "email: " + u.email + ", "

      if (pubKey != null) {
        response += "pubKey: " + u.pubKey.value + ", "

      } else {

        response += "pubKey: ,"
      }

      response += "pubKeyId: " + u.pubKeyId + " "
      response += "}, \n"

    }

    response += " status: OK }";
    render(response)
  }

  def get = {
    // by name

  }

  def changePassword = {


  }


// Needs salting!
// or use of DIGEST-MD5
  
  public static String generateValue(String s) {

// Based on an implementation from: http://www.hann3mann.de/web-artikel/anzeige/md5-hash-mit-java/

    MessageDigest md5 = null;
    try {
      md5 = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    md5.reset();
    md5.update(s.getBytes());

    byte[] result = md5.digest();

    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < result.length; i++) {
      hexString.append(Integer.toHexString(0xFF & result[i]));
    }

    return hexString.toString();
  }

}
