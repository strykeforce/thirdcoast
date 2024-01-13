package org.strykeforce.console

import edu.wpi.first.wpilibj2.command.button.Trigger
import java.util.function.BooleanSupplier

class ConsoleButton(console: Console, switch: Console.Switch) :
    Trigger(BooleanSupplier { console.getSwitch(switch) })