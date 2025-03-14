// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "BrmCapacitorBluetoothSerial",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "BrmCapacitorBluetoothSerial",
            targets: ["BrMCapacitorBluetoothSerialPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "BrMCapacitorBluetoothSerialPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/BrMCapacitorBluetoothSerialPlugin"),
        .testTarget(
            name: "BrMCapacitorBluetoothSerialPluginTests",
            dependencies: ["BrMCapacitorBluetoothSerialPlugin"],
            path: "ios/Tests/BrMCapacitorBluetoothSerialPluginTests")
    ]
)