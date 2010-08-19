import com.jaeckel.locator.Location
import grails.converters.JSON
import com.jaeckel.locator.User
import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.GrailsApplication
import com.google.appengine.api.datastore.Text
import com.jaeckel.locator.UserController
import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.EntityNotFoundException
import com.google.appengine.api.datastore.Query

class BootStrap {

  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  def init = {servletContext ->

    JSON.registerObjectMarshaller(Location) {
      def returnArray = [:]
      returnArray['encryptedPosition'] = it.encryptedPosition.value
      returnArray['timestamp'] = it.timestamp
      returnArray['status'] = it.status
      returnArray['toKey'] = it.toKey
      returnArray['fromKey'] = it.fromKey
      returnArray['keybitcount'] = it.keybitcount
      return returnArray
    }

    JSON.registerObjectMarshaller(User) {
      def returnArray = [:]
      returnArray['name'] = it.name
      returnArray['email'] = it.email
      returnArray['passwordHash'] = it.passwordHash
      returnArray['pubKey'] = it.pubKey
      returnArray['pubKeyId'] = it.pubKeyId

      return returnArray
    }

    if (GrailsUtil.getEnvironment().equals(GrailsApplication.ENV_DEVELOPMENT)) {

      println "detected ENV_DEVELOPMENT"

      if (!existsUser("Dirk")) {


        println "creating User"

        User u = new User()

        u.name = "Dirk"
        u.email = "testuser@test.do.main"
        u.pubKeyId = "123456789"
        u.pubKey = new Text("Lorem ipsum dolor sit amet")

        String password = "secret"
        if (password == null) {
          println("ERROR: need pasword parameter")
          return
        }
        u.passwordHash = UserController.generateMD5Value(password)


        if (u.validate()) {
          u.save();

          println("OK: user: " + u.name + " created.")

          if (existsUser("Dirk")) {
            println "1. User Dirk exists now!"
          } else {
            println "1. Still no user Dirk!!!! :-("
          }

          if (checkUser("Dirk")) {
            println "1. User Dirk exists now!"
          } else {
            println "1. Still no user Dirk!!!! :-("
          }

//          if (existsUser3("Dirk")) {
//            println "2. User Dirk exists now!"
//          } else {
//            println "2. Still no user Dirk!!!! :-("
//          }

//          if (existsUser2("Dirk")) {
//            println "User Dirk exists now!"
//          } else {
//            println "Still no user Dirk!!!! :-("
//          }
        }
      }


    }
    if (GrailsUtil.getEnvironment().equals(GrailsApplication.ENV_PRODUCTION)) {

      println "detected ENV_PRODUCTION"
    }

    def destroy = {
    }
  }

  def checkUser(s) {
    println("------> dataStore.get(): ")

//    try {
//      Entity userEntity = datastore.get(KeyFactory.createKey("User", 3));
//      println("------> userEntity: " + userEntity)
//    } catch (EntityNotFoundException e) {
//      println "EntityNotFoundException: " + e.getMessage()
//    }

    Query query = new Query("User").addFilter("Name", Query.FilterOperator.EQUAL, s);

    for (Entity taskEntity: datastore.prepare(query).asIterable()) {
      println "taskEntity: " + taskEntity
    }

    return false
  }

  def existsUser(s) {

    def users = User.findAllByName("Dirk")
    if (users) {
      return true
    }

    return false

  }

  def existsUser2(s) {

    println "-----> existsUser2: s: " + s
    def users = User.findAll("select from User where name='" + s + "'");
    println "-----> after findAll user: " + users

    if (users) {

      users.each {
        println "it: " + it
        if (it.name == s) {
          return true

        }
      }

    }

    return false

  }

  def existsUser3(s) {

    println "-----> existsUser2: s: " + s
    def users = User.list();
    println "-----> after findAll user: " + users

    if (users) {

      users.each {
        println "it: " + it
        if (it.name == s) {
          return true

        }
      }

    }

    return false

  }


}