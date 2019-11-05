package tripletail

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.crypto.GlobalCrypto.crypto.subtle._
import org.scalajs.dom.crypto.{CryptoKey, KeyUsage}
import org.scalajs.dom.raw.{IDBDatabase, IDBVersionChangeEvent}

import scala.scalajs.js

object LicenseeStore {
  private val dbName = "db"
  private val dbVersion = 1

  private val storeKeyPath = "{ keyPath: 'key' }"
  private val storeKey = 1

  private val cryptoStore = "crypto"
  private var cryptoKeyCache: Option[CryptoKey] = None

  private val licenseeStore = "licensee"
  private var licenseeCache: Option[Licensee] = None

  private val openDBRequest = window.indexedDB.open(dbName, dbVersion)

  openDBRequest.onupgradeneeded = (event: IDBVersionChangeEvent) => {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    db.createObjectStore(cryptoStore, storeKeyPath)
    db.createObjectStore(licenseeStore, storeKeyPath)
    val cryptoKey = generateCryptoKey()
    putCryptoKey(db, cryptoKey)
    console.log("openDBRequest.onupgradeneeded", event)
  }

  openDBRequest.onerror = (event: ErrorEvent) => console.error("openDBRequest.onerror", event)

  openDBRequest.onsuccess = (event: dom.Event) => {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    cacheCryptoKey(db)
    cacheLicensee(db)
    console.log("openDBRequest.onsuccess", event)
  }

  private def generateCryptoKey(): CryptoKey = {
    val keyAlgoId = "RSA-OAEP"
    val extractable = false
    val keyUsages = js.Array(KeyUsage.encrypt, KeyUsage.decrypt)
    generateKey(keyAlgoId, extractable, keyUsages).valueOf().asInstanceOf[CryptoKey]
  }

  private def putCryptoKey(db: IDBDatabase, cryptoKey: CryptoKey): Unit = {
    val store = db.transaction(cryptoStore, "readwrite").objectStore(cryptoStore)
    store.put(cryptoKey, storeKey)
  }

  private def cacheCryptoKey(db: IDBDatabase): Unit = {
    val store = db.transaction(cryptoStore, "readonly").objectStore(cryptoStore)
    val dbRequest = store.get(storeKey)
    dbRequest.onerror = (event: ErrorEvent) => console.error("cacheCryptoKey", event)
    dbRequest.onsuccess = (event: dom.Event) => {
      val cryptoKey = dbRequest.result.asInstanceOf[CryptoKey]
      cryptoKeyCache = Some(cryptoKey)
      console.log("cacheCryptoKey: db request => on success", event)
    }
  }

  private def cacheLicensee(db: IDBDatabase): Unit = {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    val store = db.transaction(licenseeStore, "readonly").objectStore(licenseeStore)
    val dbRequest = store.get(storeKey)
    dbRequest.onerror = (event: ErrorEvent) => console.error("cacheLicensee", event)
    dbRequest.onsuccess = (event: dom.Event) => {
      val licensee = dbRequest.result.asInstanceOf[Licensee]
      licenseeCache = Some(licensee)
      console.log("cacheLicensee: db request => on success", event)
    }
  }
}