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

      if (!userWithNameExists("Fnord")) {


        println "creating User Fnord"

        User u = new User()

        u.name = "Fnord"
        u.email = "fnord@foo.do.main"
        u.pubKeyId = "123456789"
        u.pubKey = new Text("Nochmal Lorem ipsum dolor sit amet")

        String password = "secret"
        if (password == null) {
          println("ERROR: need pasword parameter")
          return
        }
        u.passwordHash = UserController.generateMD5Value(password)


        if (u.validate()) {
          u.save();

          println("OK: user: " + u.name + " created.")

          if (userWithNameExists("Fnord")) {
            println "1. User Fnord exists now!"
          } else {
            println "1. Still no user Fnord!!!! :-("
          }

          listUsers()

        }
      }


    }
    if (GrailsUtil.getEnvironment().equals(GrailsApplication.ENV_PRODUCTION)) {

      println "detected ENV_PRODUCTION"
    }

    def destroy = {
    }
  }

  def userWithNameExists(s) {
    println("------> dataStore.get(): s: " + s)

    Query query = new Query("User").addFilter("name", Query.FilterOperator.EQUAL, s);

    for (Entity taskEntity: datastore.prepare(query).asIterable()) {
      println "taskEntity: " + taskEntity

      return true
    }

    return false
  }

  def listUsers() {
    def result = []

    Query query = new Query("User");

    for (Entity user: datastore.prepare(query).asIterable()) {
      println "taskEntity: " + user

      result.add(user.getProperties())

    }

    return result
  }

}