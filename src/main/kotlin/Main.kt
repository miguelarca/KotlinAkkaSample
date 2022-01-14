
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive


sealed interface Command
object Increment: Command
object Decrement: Command

class Tracker (context: ActorContext<Command>): AbstractBehavior<Command>(context) {
    private var currentValue: Int = 0

    companion object {
        fun create(): Behavior<Command> = Behaviors.setup { context: ActorContext<Command> ->
            Tracker(context)
        }
    }


    override fun createReceive(): Receive<Command> {
        return newReceiveBuilder()
            .onMessage(Command::class.java, this::processCommand)
            .build()
    }

    private fun processCommand(command: Command): Behavior<Command> {
        when(command) {
            Increment -> {
                currentValue += 1
                println("Incrementing the value by one. Current value is: $currentValue.")
            }
            Decrement -> {
                currentValue -= 1
                println("Decrementing the value by one. Current value is: $currentValue.")
            }
        }

        return Behaviors.same()
    }
}


fun main(args: Array<String>) {
    val system = ActorSystem.create(Tracker.create(),"akka-example")

    try {
        println("Enter a message:")
        var message = readLine()

        while ( message != "terminate") {
            if (message != "Increment" && message != "Decrement") {
                println("Wrong command")
            }

            if (message == "Increment") {
                system.tell(Increment)
            }

            if (message == "Decrement") {
                system.tell(Decrement)
            }

            message = readLine()
        }

        system.terminate()
    } finally {
        system.terminate()
    }
}