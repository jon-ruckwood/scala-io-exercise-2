package com.xebia
package exercise2

import akka.actor.{ActorRef, Props, ActorContext}

trait CreationSupport {

  def getChild(name:String):Option[ActorRef]
  def createChild(props:Props, name:String):ActorRef
  def getOrCreateChild(props:Props, name:String):ActorRef = getChild(name).getOrElse(createChild(props, name))
}

trait ActorContextCreationSupport extends CreationSupport {
    def context:ActorContext

    def getChild(name: String) = context.child(name)

    def createChild(props: Props, name: String) = context.actorOf(props, name)
}
