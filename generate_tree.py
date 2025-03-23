import os

def generate_tree_with_content(directory, output_file, max_file_size_kb=100):
    with open(output_file, "w", encoding="utf-8") as f:
        for root, dirs, files in os.walk(directory):
            level = root.replace(directory, "").count(os.sep)
            indent = "│   " * level + "├── " if level > 0 else ""
            f.write(f"{indent}{os.path.basename(root)}/\n")

            sub_indent = "│   " * (level + 1)
            for file in files:
                file_path = os.path.join(root, file)
                f.write(f"{sub_indent}├── {file}\n")

                # Kiểm tra kích thước file (chỉ đọc nếu nhỏ hơn max_file_size_kb KB)
                if os.path.getsize(file_path) / 1024 <= max_file_size_kb:
                    try:
                        with open(file_path, "r", encoding="utf-8") as file_content:
                            f.write("\n" + "-"*50 + f"\n│--- Nội dung file: {file} ---\n")
                            for line in file_content:
                                f.write("│ " + line)
                            f.write("-"*50 + "\n\n")
                    except Exception as e:
                        f.write(f"│ (Không thể đọc file: {e})\n")

if __name__ == "__main__":
    project_path = "./src"  # Đổi thư mục nếu cần
    output_file = "tree_with_content.txt"
    generate_tree_with_content(project_path, output_file)
    print(f"Đã tạo {output_file} chứa cây thư mục và nội dung file.")
