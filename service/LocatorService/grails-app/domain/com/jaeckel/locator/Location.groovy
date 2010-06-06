package com.jaeckel.locator



import javax.persistence.*;
// import com.google.appengine.api.datastore.Key;

@Entity
class Location implements Serializable {

  @Id
  @GeneratedValue (strategy = GenerationType.IDENTITY)
  Long id

  static constraints = {
    id visible: false
  }

  String encryptedPosition
  Date timestamp
  String status

  public String toString() {
    return "Location{" +
            "id=" + id +
            ", encryptedPosition='" + encryptedPosition + '\'' +
            ", timestamp=" + timestamp +
            ", status='" + status + '\'' +
            '}';
  }


}
