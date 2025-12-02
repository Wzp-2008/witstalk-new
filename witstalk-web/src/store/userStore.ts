import { create } from "zustand";
import { persist } from 'zustand/middleware';
import { userInfo } from '~/api/user';

interface UserStoreState {
    userInfo: {
        id: null | string;
        nickName: null | string;
        email: null | string;
        username: null | string;
        avatar: null | string;
        createTime: null | string;
        updateTime: null | string;
    };
    permissions: string[];
}

interface UserStore extends UserStoreState{
    updateUserInfo: () => Promise<void>;
    restartUserInfo: () => void;
    setPermissions: (permissions: string[]) => void;
    hasPermission: (permission: string) => boolean;
}

export const useUserStore = create(
  persist<UserStore>(
    (set, get) => ({
      userInfo: {
        id: null,
        nickName: null,
        email: null,
        username: null,
        avatar: null,
        createTime: null,
        updateTime: null,
      },
      permissions: [],
      updateUserInfo: async() => {
        const res = await userInfo();
        set({
          userInfo: {
            id: res.data.id || null,
            nickName: res.data.nickName || null,
            email: res.data.email || null,
            username: res.data.username || null,
            avatar: res.data.avatar || null,
            createTime: res.data.createTime || null,
            updateTime: res.data.updateTime || null,
          },
          permissions: res.data.permissions || []
        });
      },
      restartUserInfo: () => {
        set({
          userInfo: {
            id: null,
            nickName: null,
            email: null,
            username: null,
            avatar: null,
            createTime: null,
            updateTime: null,
          },
          permissions: []
        });
      },
      // 设置权限列表
      setPermissions: (permissions: string[]) => {
        set({ permissions });
      },
      // 检查是否有权限
      hasPermission: (permission: string) => {
        const { permissions } = get();
        return permissions.includes(permission);
      }
    }),
    {
      name: 'user-info',
      storage: {
        getItem: (name) => {
          const item = sessionStorage.getItem(name);
          return item ? JSON.parse(item) : null;
        },
        setItem: (name, value) => {
          const serialized = JSON.stringify(value);
          sessionStorage.setItem(name, serialized);
        },
        removeItem: (name) => {
          sessionStorage.removeItem(name);
        }
      },
      partialize: (state: UserStore): Pick<UserStore, 'userInfo' | 'permissions'> => ({ userInfo: state.userInfo, permissions: state.permissions }),
    }
  )
);
