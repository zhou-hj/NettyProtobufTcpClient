package cn.xiaosheng996.NettyProtobufTcpClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Packet packet = (Packet)msg;

		Class<?> clz = ProtoManager.getRespMap().get(packet.getCmd());
		try {
			Method method = clz.getMethod("parseFrom", byte[].class);
			Object object = method.invoke(clz, packet.getBytes());
			
//			LoginResp_1001001 req = (LoginResp_1001001)object;
//			System.out.println(req.getAccount());
//			System.out.println(req.getRid());
//			System.out.println(req.getLevel());
			
			ProtoPrinter.print(object);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.channel().close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (NettyTcpClient.instance().getChannel() != null) {
			NettyTcpClient.instance().getChannel().disconnect();
		}
		NettyTcpClient.instance().setChannel(ctx.channel());
		System.out.println("登录  " + ctx.channel().remoteAddress().toString().replace("/", "") + "  成功");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("\n断开  " + ctx.channel().remoteAddress().toString().replace("/", "") + "  连接");
	}

}
