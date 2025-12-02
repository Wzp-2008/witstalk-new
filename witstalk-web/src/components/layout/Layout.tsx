import {Outlet} from "react-router";
import { Flex, Layout} from "antd";
import TopMenu from "~/components/layout/TopMenu";
import RightTab from "~/components/layout/RightTab";
import CustomFooter from "~/components/layout/Footer";

const {Header, Footer, Content} = Layout;

export default function AppLayout() {
    return (
        <Flex gap="middle" wrap>
            <Layout style={{height: "100%", width: "100vw", background: "linear-gradient(135deg, var(--cream-bg-1) 0%, var(--cream-bg-2) 100%)"}}>
                <Header style={{ background: "var(--cream-light)", paddingBottom: '10px' }}>
                    <div style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center"
                    }}>
                        <div>
                            <TopMenu/>
                        </div>
                        <div>
                            <RightTab/>
                        </div>
                    </div>
                </Header>
                <Content style={{height: "86vh", maxHeight: "86vh"}}>
                    <Outlet/>
                </Content>
                <Footer style={{ background: "var(--cream-light)" }}>
                    <CustomFooter/>
                </Footer>
            </Layout>
        </Flex>
    )
}
