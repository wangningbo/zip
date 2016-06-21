package com.icircall.zip;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class IOServer {

	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		try (ServerSocketChannel server = ServerSocketChannel.open()) {
			server.bind(new InetSocketAddress("0.0.0.0", 9000));
			try (SocketChannel c = server.accept()) {
				FileChannel f = FileChannel.open(Paths.get("/home/wang/Documents/apache-groovy-sdk-2.4.6.zip"),
						StandardOpenOption.READ);
				long offset = 0;
				long limit = f.size();
				String header = "HTTP/1.1 200 OK\nContent-Type:application/zip\nContent-Length:" + limit + "\n\n";
				ByteBuffer buf = ByteBuffer.wrap(header.getBytes());
				while (buf.hasRemaining()) {
					c.write(buf);
				}
				// c.setOption(StandardSocketOptions.SO_SNDBUF, 0);
				System.out.println("SND_BUF:" + c.getOption(StandardSocketOptions.SO_SNDBUF));
				c.setOption(StandardSocketOptions.TCP_NODELAY, true);
				c.configureBlocking(false);
				c.register(selector, SelectionKey.OP_WRITE);
				while (offset < limit) {
					selector.select();
					selector.selectedKeys().clear();
					long writed = f.transferTo(offset, limit - offset, c);
					offset += writed;
//					System.out.println("--" + writed);
				}
			}
		}
	}
}
