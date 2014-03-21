package com.xebia
package exercise2

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

import spray.routing._

import akka.actor.{Props, ActorRef}
import akka.pattern.ask
import akka.util.Timeout

import spray.httpx.SprayJsonSupport._

class Receptionist extends HttpServiceActor
                      with ReverseRoute
                      with ActorContextCreationSupport {
  implicit def executionContext = context.dispatcher

  def receive = runRoute(reverseRoute)
}

trait ReverseRoute extends HttpService with CreationSupport {
  implicit def executionContext: ExecutionContext

  import ReverseActor._
  private val reverseActor = createChild(props, name)

  def reverseRoute:Route = path("reverse") {
    post {
      entity(as[ReverseRequest]) { request =>
        implicit val timeout = Timeout(20 seconds)

        val futureResponse = reverseActor.ask(Reverse(request.value))
                                         .map {
                                           case PalindromeResult     => ReverseResponse(request.value, true)
                                           case ReverseResult(value) => ReverseResponse(value)
                                         }

        complete(futureResponse)
      }
    }
  }
}
