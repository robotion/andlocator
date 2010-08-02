package com.jaeckel.locator

import com.jaeckel.locator.User
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.google.appengine.api.datastore.Text

class UserController {

  def index = {

    render("OK: HELO")


  }

  def create = {

    if (request.isSecure()) {

      def users = User.findAll("select  from User where name='" + params.name + "'");

      if (users.size() > 0) {
        render("ERROR: name not unique.")
        return
      }
      User u = new User()

      u.name = params.name
      u.email = params.email
      u.pubKeyId = params.pubKeyId
      u.pubKey = new Text(params.pubKey)
      String password = params.password
      if (password == null) {
        render("ERROR: need pasword parameter")
        return
      }
      u.passwordHash = generateMD5Value(password)


      if (u.validate()) {
        u.save();

        render("OK: user: " + u.name + " created.")
        return
      }

      render("ERROR: user: " + u + " not valid.")


    } else {

      render("ERROR: no SSL")

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

      if (u.pubKey != null) {
        response += "pubKey: " + u.pubKey.value + ", "

      } else {

        response += "pubKey: ,"
      }


      response += "passwordHash: " + u.passwordHash + ", "

      response += "pubKeyId: " + u.pubKeyId + " "
      response += "}, \n"

    }

    response += " status: OK }";
    render(response)
  }

  def get = {
    // by name
    if (params.name != null && params.name != "") {

      log.error("Search by keyid: " + params.name)

      def users = User.findAll("select  from User where name='" + params.name + "'");

      def response = "{\n";

      users.each {User u ->
        response += "{ "
        response += "id: " + u.id + ", "
        response += "name: " + u.name + ", "
        response += "email: " + u.email + ", "

        if (u.pubKey != null) {
          response += "pubKey: " + u.pubKey.value + ", "

        } else {

          response += "pubKey: ,"
        }


        response += "passwordHash: " + u.passwordHash + ", "

        response += "pubKeyId: " + u.pubKeyId + " "
        response += "}, \n"

      }
      response += "}";
      render(response)

      return

    }
  }

  def changePassword = {

    def passwordHash = generateMD5Value(params.password)


  }

// Needs salting!
// or use DIGEST-MD5

  public static String generateMD5Value(String s) {

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
