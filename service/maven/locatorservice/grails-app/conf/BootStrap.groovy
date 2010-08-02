import com.jaeckel.locator.Location
import grails.converters.JSON
import com.jaeckel.locator.User

class BootStrap {

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
  }
  def destroy = {
  }
}
