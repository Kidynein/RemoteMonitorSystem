package client;

import common.Message;
import common.MessageType;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatcher extends Thread {
    private final ClientMain client;
    private final Path rootPath;
    private WatchService watcher;
    private final Map<WatchKey, Path> keys; // Map để lưu Key và Đường dẫn tương ứng
    private boolean isRunning = true;

    public DirectoryWatcher(ClientMain client, String pathStr) throws IOException {
        this.client = client;
        this.rootPath = Paths.get(pathStr);
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();

        // Đăng ký giám sát thư mục gốc và TẤT CẢ thư mục con
        registerAll(this.rootPath);
    }

    // Duyệt cây thư mục và đăng ký giám sát cho từng folder tìm thấy
    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    // Đăng ký 1 thư mục cụ thể
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir); // Lưu lại để tra cứu: Key này thuộc về Folder nào?
        System.out.println("-> Đang giám sát thư mục: " + dir);
    }

    @Override
    public void run() {
        System.out.println("Bắt đầu theo dõi: " + rootPath);

        while (isRunning) {
            WatchKey key;
            try {
                // take() là hàm block: Nó sẽ dừng ở đây chờ đến khi có sự kiện xảy ra
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("Không nhận diện được WatchKey!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // Bỏ qua sự kiện OVERFLOW (tràn bộ đệm)
                if (kind == OVERFLOW) continue;

                // Lấy tên file bị tác động
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                Path child = dir.resolve(fileName);

                // Gửi thông báo về Server
                String action = kind.name();
                String messageContent = child.toString();

                // Lấy tên client hiện tại để gửi kèm
                String senderName = client.getClientName();

                Message msg = new Message(MessageType.FILE_EVENT, senderName, messageContent, action);
                client.sendMessage(msg);

                // Logic giám sát đệ quy:
                // Nếu sự kiện là TẠO MỚI (ENTRY_CREATE) và cái mới tạo là THƯ MỤC
                // -> Thì phải đăng ký giám sát luôn thư mục mới đó.
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        x.printStackTrace();
                    }
                }
            }

            // Reset key để tiếp tục nhận sự kiện lần sau
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key); // Nếu thư mục bị xóa thì bỏ theo dõi
                if (keys.isEmpty()) {
                    break; // Nếu không còn thư mục nào để theo dõi thì dừng
                }
            }
        }
    }

    public void stopWatching() {
        isRunning = false;
        try {
            if (watcher != null) watcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}