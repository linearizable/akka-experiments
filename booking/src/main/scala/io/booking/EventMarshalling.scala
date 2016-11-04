package io.booking

import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

//<start id="ch02_rest_messages"/>
// case class EventDescription(tickets: Int) { //<co id="ch02_rest_event_decription"/>
//   require(tickets > 0)
// }
//
// case class TicketRequest(tickets: Int) { //<co id="ch02_rest_ticket_request"/>
//   require(tickets > 0)
// }

case class Error(message: String) //<co id="ch02_rest_error"/>
//<end id="ch02_rest_messages"/>

trait EventMarshalling  extends SprayJsonSupport with DefaultJsonProtocol {
  import actors._

  // implicit val eventDescriptionFormat = jsonFormat1(EventDescription)
  implicit val hallFormat = jsonFormat3(Hall)
  implicit val hallsFormat = jsonFormat1(Halls)
  implicit val cinemaFormat = jsonFormat3(Cinema)
  implicit val cinemasFormat = jsonFormat1(Cinemas)
  // implicit val ticketRequestFormat = jsonFormat1(TicketRequest)
  // implicit val ticketFormat = jsonFormat1(TicketSeller.Ticket)
  // implicit val ticketsFormat = jsonFormat2(TicketSeller.Tickets)
  // implicit val errorFormat = jsonFormat1(Error)
}
