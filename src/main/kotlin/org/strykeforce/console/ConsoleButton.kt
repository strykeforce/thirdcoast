package org.strykeforce.console

import edu.wpi.first.wpilibj2.command.button.Button
import java.util.function.BooleanSupplier

class ConsoleButton(console: Console, switch: Console.Switch) :
    Button(BooleanSupplier { console.getSwitch(switch) })