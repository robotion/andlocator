import com.jaeckel.locator.User

/**
 * User: biafra
 * Date: Aug 18, 2010
 * Time: 10:08:27 PM
 */
class SecurityFilters {
  def filters = {


//    all(controller: '*', action: '*') {
//      before = {
//        println("all.before")
//        println("params: " + params)
//        return true
//
//      }
//    }

    userCheck(controller: 'user', action: '*') {
      before = {
        println("userCheck.before()...")
//        checkUser()
//        def users = User.findAll("select  from User where name='" + params.name + "'");
//        println("User: " + users)

        return true

      }
    }

    restCheck(controller: 'rest', action: '*') {
      before = {
        println("restCheck.before()...")
//        checkUser()
        return true
      }
    }

  }
}