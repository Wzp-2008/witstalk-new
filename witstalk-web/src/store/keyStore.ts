import {create} from "zustand/react";

interface keyStoreInterface {
    key1: string;
    key2: string | null;
    key3: string | null;
    removeKeys: () => void;
}

export const useKeyStore = create<keyStoreInterface>((setState) => ({
    key1: import.meta.env.VITE_APP_KEY1,
    key2: null,
    key3: null,
    removeKeys: () => setState({key1: import.meta.env.VITE_APP_KEY1, key2: null, key3: null})
}))
