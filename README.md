# brm-capacitor-bluetooth-serial

Plugin to work with ionic and capacitor using bluetooth

## Install

```bash
npm install brm-capacitor-bluetooth-serial
npx cap sync
```

## API

<docgen-index>

* [`requestPermissions()`](#requestpermissions)
* [`hasPermitions()`](#haspermitions)
* [`listPairedDevices()`](#listpaireddevices)
* [`connect(...)`](#connect)
* [`disconnect(...)`](#disconnect)
* [`isEnabled()`](#isenabled)
* [`isConnected(...)`](#isconnected)
* [`write(...)`](#write)
* [`read(...)`](#read)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### requestPermissions()

```typescript
requestPermissions() => void
```

--------------------


### hasPermitions()

```typescript
hasPermitions() => Promise<{ hasPermitions: boolean; }>
```

**Returns:** <code>Promise&lt;{ hasPermitions: boolean; }&gt;</code>

--------------------


### listPairedDevices()

```typescript
listPairedDevices() => Promise<{ devices: any[]; }>
```

**Returns:** <code>Promise&lt;{ devices: any[]; }&gt;</code>

--------------------


### connect(...)

```typescript
connect(options: { address: string; mode: EditorMode; }) => Promise<{ connected: boolean; }>
```

| Param         | Type                                                                          |
| ------------- | ----------------------------------------------------------------------------- |
| **`options`** | <code>{ address: string; mode: <a href="#editormode">EditorMode</a>; }</code> |

**Returns:** <code>Promise&lt;{ connected: boolean; }&gt;</code>

--------------------


### disconnect(...)

```typescript
disconnect(options: { address: string; }) => Promise<{ disconnected: boolean; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

**Returns:** <code>Promise&lt;{ disconnected: boolean; }&gt;</code>

--------------------


### isEnabled()

```typescript
isEnabled() => Promise<{ isEnabled: boolean; }>
```

**Returns:** <code>Promise&lt;{ isEnabled: boolean; }&gt;</code>

--------------------


### isConnected(...)

```typescript
isConnected(options: { address: string; }) => Promise<{ isConnected: boolean; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

**Returns:** <code>Promise&lt;{ isConnected: boolean; }&gt;</code>

--------------------


### write(...)

```typescript
write(options: { address: string; command: string; }) => Promise<void>
```

| Param         | Type                                               |
| ------------- | -------------------------------------------------- |
| **`options`** | <code>{ address: string; command: string; }</code> |

--------------------


### read(...)

```typescript
read(options: { address: string; }) => Promise<{ data: boolean; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

**Returns:** <code>Promise&lt;{ data: boolean; }&gt;</code>

--------------------


### Enums


#### EditorMode

| Members    | Value               |
| ---------- | ------------------- |
| **`TEXT`** | <code>"TEXT"</code> |
| **`HEX`**  | <code>"HEX"</code>  |

</docgen-api>
