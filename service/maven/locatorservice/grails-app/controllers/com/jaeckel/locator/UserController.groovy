package com.jaeckel.locator

import com.jaeckel.locator.User
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.google.appengine.api.datastore.Text
import grails.converters.JSON
import javax.servlet.http.HttpUtils

class UserController {

  def index = {

    render("{OK: HELO}")


  }

  def create = {
    
    URL url =  new URL(HttpUtils.getRequestURL(request).toString())
    log.error("request.hostName: " + url.host)

    if (request.isSecure() || url.host == "localhost") {

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

    render(users as JSON)
  }

  def get = {
    // by name
    if (params.name != null && params.name != "") {

      log.error("Search by name: " + params.name)

      def users = User.findAll("select  from User where name='" + params.name + "'");

      render(users as JSON)

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
