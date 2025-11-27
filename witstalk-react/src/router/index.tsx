import { createBrowserRouter, RouterProvider} from "react-router";
import {AppLayout} from "~/components/layout/Layout.tsx";
import {ErrorBoundary} from "~/components/ErrorBoundary.tsx";
import React, {Suspense} from "react";

const AppIndex = React.lazy(() => import("~/pages/index.tsx"));
const LoginPage = React.lazy(() => import("~/pages/login/index.tsx"));
const RegisterPage = React.lazy(() => import("~/pages/register/index.tsx"));

const router = createBrowserRouter([
    {
        path: "/",
        element: (
            <Suspense fallback={<div>加载中...</div>}>
                <AppLayout>children</AppLayout>
            </Suspense>
        ),
        errorElement: <ErrorBoundary/>,
        children: [
            {
                index: true, element: (
                    <Suspense fallback={<div>加载中...</div>}>
                        <AppIndex/>
                    </Suspense>
                ),
            }
        ]
    },
    {
        path: "/login",
        element: (
            <Suspense fallback={<div>加载中...</div>}>
                <LoginPage/>
            </Suspense>
        ),
        errorElement: <ErrorBoundary/>
    },
    {
        path: "/register",
        element: (
            <Suspense fallback={<div>加载中...</div>}>
                <RegisterPage/>
            </Suspense>
        ),
        errorElement: <ErrorBoundary/>
    }
])

export const AppRouter = () => {
    return (
        <RouterProvider router={router} />
    );
}
