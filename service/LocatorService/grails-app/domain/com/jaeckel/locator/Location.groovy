package com.jaeckel.locator



import javax.persistence.*;
import com.google.appengine.api.datastore.Text;
//import com.google.appengine.api.datastore.Key;

@Entity
class Location implements Serializable {


  @Id
  @GeneratedValue (strategy = GenerationType.IDENTITY)
  Long id

  static constraints = {
    id visible: false
  }
//  static mapping = {
//    columns {
//      encryptedPosition type:'com.google.appengine.api.datastore.Text'
//
//    }
//  }

  Text encryptedPosition

  Date timestamp
  String status
  String keyid
  Integer keybitcount = 0


  public String toString() {
    return "Location{" +
            "id=" + id +
            ", encryptedPosition='" + encryptedPosition.value + '\'' +
            ", timestamp=" + timestamp +
            ", status='" + status + '\'' +
            ", keyid='" + keyid + '\'' +
            ", keybitcount=" + keybitcount +
            '}';
  }

}
