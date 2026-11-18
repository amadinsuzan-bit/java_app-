package com.exg.mygame.chain;

/// Everything that touches a chain private key, behind one interface.
///
/// [SeedSigner] keeps the seed in this process, which is right for a test
/// network and wrong for real money. A product implements this over an HSM or
/// a cloud key service that holds secp256k1 keys and signs a 32-byte digest
/// (AWS KMS and Google Cloud KMS both offer `ECC_SECG_P256K1`): the key then
/// never exists in the server's memory, and every signature is logged by a
/// system the application cannot rewrite. The service answers DER; recover
/// the id by trying both values against the known public key.
public interface ChainSigner {
    /// The address withdrawals are paid from.
    String hotAddress();

    /// The customer deposit address at `index`.
    String depositAddress(long index);

    /// r || s || recovery id over `hash32`, with the hot wallet key.
    byte[] signWithHot(byte[] hash32);

    /// The same with the deposit key at `index`, to sweep it.
    byte[] signWithDeposit(long index, byte[] hash32);
}
