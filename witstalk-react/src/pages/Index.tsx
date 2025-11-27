import {request, requestSWR} from "~/util/request";
import {message} from "antd";
import {useMessage} from "~/util/msg.tsx";

export default function AppIndex () {
    const [messageApi, contextHolder] = message.useMessage();
    const {data, isLoading} = requestSWR({
        url: '/system/error',
        method: 'post',
        data: {
            userId: 123,
            username: "张三"
        }
    })
    console.log(data, isLoading);

    const onclick = () => {
        useMessage().success("dasds")
    }

    return (
        <div>
            {contextHolder}
            <button onClick={onclick}>
                点击显示消息
            </button>
        </div>
    )
}
