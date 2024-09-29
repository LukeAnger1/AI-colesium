import os
import shutil

def copy_and_replace_package(src_folder, dest_folder, old_name, new_name):
    # Copy the entire folder to the new location
    shutil.copytree(src_folder, dest_folder)
    
    # Walk through the copied folder
    for root, dirs, files in os.walk(dest_folder):
        # Replace directory names
        for dir_name in dirs:
            if old_name in dir_name:
                new_dir_name = dir_name.replace(old_name, new_name)
                os.rename(os.path.join(root, dir_name), os.path.join(root, new_dir_name))
        
        # Replace content in files
        for file_name in files:
            file_path = os.path.join(root, file_name)
            
            # Open each file and read its content
            with open(file_path, 'r', encoding='utf-8') as file:
                content = file.read()
            
            # Replace occurrences of the old name with the new name
            updated_content = content.replace(old_name, new_name)
            
            # Write the updated content back to the file
            with open(file_path, 'w', encoding='utf-8') as file:
                file.write(updated_content)
                
            # Rename the file if it contains the old package name
            if old_name in file_name:
                new_file_name = file_name.replace(old_name, new_name)
                os.rename(file_path, os.path.join(root, new_file_name))

# Example usage
src_folder = input('path/to/old/package')  # Replace with the source package folder
dest_folder = input('path/to/new/package') # Replace with the destination folder
old_name = src_folder # 'OldPackageName'         # Replace with the old package name
new_name = dest_folder # 'NewPackageName'         # Replace with the new package name

copy_and_replace_package(src_folder, dest_folder, old_name, new_name)
