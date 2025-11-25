import {create} from "zustand/react";

export const keyStore = create((setState) => ({
    // public key
    key1: import.meta.env.VITE_APP_KEY1 as string | null,
    // aes key
    key2: null as string | null,
    // aes iv
    key3: null as string | null,
    removeKeys: () => setState({key1: null, key2: null, key3: null})
}))
