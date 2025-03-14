import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(BrMCapacitorBluetoothSerialPlugin)
public class BrMCapacitorBluetoothSerialPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "BrMCapacitorBluetoothSerialPlugin"
    public let jsName = "BrMCapacitorBluetoothSerial"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = BrMCapacitorBluetoothSerial()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
}
