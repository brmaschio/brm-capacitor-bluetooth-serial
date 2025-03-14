import Foundation

@objc public class BrMCapacitorBluetoothSerial: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
