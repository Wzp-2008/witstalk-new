import { createBrowserRouter, RouterProvider } from "react-router";
import React, { Suspense } from "react";
import { Spin } from "antd";
import { ErrorBoundary } from "~/components/ErrorBoundary.tsx";

// 懒加载组件
const AppLayout = React.lazy(() => import("~/components/layout/Layout.tsx"));
const AppIndex = React.lazy(() => import("~/pages/index.tsx"));
const LoginPage = React.lazy(() => import("~/pages/login/index.tsx"));
const RegisterPage = React.lazy(() => import("~/pages/register/index.tsx"));
const WitstalkPage = React.lazy(() => import("~/pages/witstalk/index.tsx"));
const FilePage = React.lazy(() => import("~/pages/file/index.tsx"));
const GamePage = React.lazy(() => import("~/pages/game/index.tsx"));
const SystemPage = React.lazy(() => import("~/pages/system/index.tsx"));
const UserPage = React.lazy(() => import("~/pages/system/user/index.tsx"));
const MenuPage = React.lazy(() => import("~/pages/system/menu/index.tsx"));
const RolePage = React.lazy(() => import("~/pages/system/role/index.tsx"));
const DictPage = React.lazy(() => import("~/pages/system/dict/index.tsx"));
const ProfilePage = React.lazy(() => import("~/pages/system/user/profile.tsx"));
const ChannelPage = React.lazy(() => import("~/pages/system/channel/index.tsx"));

const loading = () => {
    return (<Spin size="large" className="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2" />)
}

const router = createBrowserRouter([
    {
        path: "/",
        element: (
            <Suspense fallback={loading()}>
                <AppLayout> </AppLayout>
            </Suspense>
        ),
        errorElement: <ErrorBoundary error={{ status: 500 }} />,
        children: [
            {
                index: true,
                element: (
                    <Suspense fallback={loading()}>
                        <AppIndex/>
                    </Suspense>
                ),
            },
            {
                path: "witstalk",
                element: (
                    <Suspense fallback={loading()}>
                        <WitstalkPage/>
                    </Suspense>
                ),
            },
            {
                path: "file",
                element: (
                    <Suspense fallback={loading()}>
                        <FilePage/>
                    </Suspense>
                ),
            },
            {
                path: "game",
                element: (
                    <Suspense fallback={loading()}>
                        <GamePage/>
                    </Suspense>
                ),
            },
            {
                path: "system",
                element: (
                    <Suspense fallback={loading()}>
                        <SystemPage/>
                    </Suspense>
                )
            },
            {
                path: "system/user",
                element: (
                    <Suspense fallback={loading()}>
                        <UserPage/>
                    </Suspense>
                ),
            },
            {
                path: "system/menu",
                element: (
                    <Suspense fallback={loading()}>
                        <MenuPage/>
                    </Suspense>
                ),
            },
            {
                path: "system/role",
                element: (
                    <Suspense fallback={loading()}>
                        <RolePage/>
                    </Suspense>
                ),
            },
            {
                path: "system/dict",
                element: (
                    <Suspense fallback={loading()}>
                        <DictPage/>
                    </Suspense>
                ),
            },
            {
                path: "system/channel",
                element: (
                    <Suspense fallback={loading()}>
                        <ChannelPage/>
                    </Suspense>
                ),
            }
        ]
    },
    {
        path: "/profile",
        element: (
            <Suspense fallback={loading()}>
                <ProfilePage/>
            </Suspense>
        ),
        errorElement: <ErrorBoundary error={{ status: 500 }} />
    },
    {
        path: "/login",
        element: (
            <Suspense fallback={loading()}>
                <LoginPage/>
            </Suspense>
        ),
        errorElement: <ErrorBoundary error={{ status: 500 }} />
    },
    {
        path: "/register",
        element: (
            <Suspense fallback={loading()}>
                <RegisterPage/>
            </Suspense>
        ),
        errorElement: <ErrorBoundary error={{ status: 500 }} />
    }
])

export const AppRouter = () => {
    return (
        <RouterProvider router={router} />
    );
}
