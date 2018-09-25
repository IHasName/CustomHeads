package de.mrstein.customheads.loader;

import de.mrstein.customheads.CustomHeads;
import de.mrstein.customheads.category.BaseCategory;
import de.mrstein.customheads.category.Category;
import de.mrstein.customheads.category.SubCategory;
import de.mrstein.customheads.utils.ItemEditor;
import de.mrstein.customheads.utils.JsonFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/*
 *  Project: CustomHeads in CategoryLoader
 *     by LikeWhat
 */

@Getter
public class CategoryLoader {

    @Getter
    private static boolean loaded;

    private HashMap<String, Category> categories = new HashMap<>();
    private HashMap<String, SubCategory> subCategories = new HashMap<>();
    private HashMap<Category, File> sourceFiles = new HashMap<>();

    private String language;

    private File langRootDir;

    public CategoryLoader(String language) {
        loaded = false;
        this.language = language;
        langRootDir = new File("plugins/CustomHeads/language/" + language + "/categories");

        loaded = false;
        int loaded = 0;
        int ignored = 0;
        int invalid = 0;
        if (!CustomHeads.hasReducedDebug())
            CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chPrefix + "Loading " + CustomHeads.getCategoryLoaderConfig().get().getList("categories").size() + " Categories from " + language + "/categories");
        long timestamp = System.currentTimeMillis();
        CustomHeads.getCategoryLoaderConfig().reload();
        boolean ignoreInvalid = CustomHeads.getCategoryLoaderConfig().get().getBoolean("ignoreInvalid");

        File langRootDir = new File("plugins/CustomHeads/language/" + language + "/categories");
        if (langRootDir.listFiles() == null) {
            CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chWarning + "No Categories found in language/" + language + "/categories");
            CategoryLoader.loaded = true;
            return;
        }

        List<File> fileList = Arrays.stream(langRootDir.listFiles()).filter(file -> file.getName().endsWith(".json") && CustomHeads.getCategoryLoaderConfig().get().getList("categories").contains(file.getName().substring(0, file.getName().lastIndexOf(".")))).collect(Collectors.toList());

        for (File file : fileList) {
            JsonFile jsf = new JsonFile(file);
            ignored = CustomHeads.getCategoryLoaderConfig().get().getList("categories").size() - fileList.size();
            try {
                Category category = Category.getConverter().fromJson(jsf.getJson(), Category.class);
                if (category == null) {
                    if (ignoreInvalid) {
                        invalid++;
                        Bukkit.getServer().getConsoleSender().sendMessage(CustomHeads.chWarning + " Invalid Category in " + file.getName());
                    } else {
                        throw new NullPointerException("Invalid Category in " + file.getName());
                    }
                } else {
                    if (categories.containsKey(category.getId())) {
                        CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chWarning + file.getName() + ": §cAn Category with ID " + category.getId() + " (" + categories.get(category.getId()).getName() + ") already exists.");
                        invalid++;
                        continue;
                    }
                    categories.put(category.getId(), category);
                    sourceFiles.put(category, file);
                    loaded++;
                    if (category.hasSubCategories()) {
                        for (SubCategory subCategory : category.getSubCategories()) {
                            if (subCategories.containsKey(subCategory.getId())) {
                                CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chWarning + file.getName() + ": §cAn Subcategory with ID " + subCategory.getId() + " (" + subCategories.get(subCategory.getId()).getName() + ") already exists.");
                                continue;
                            }
                            subCategories.put(subCategory.getId(), subCategory);
                        }
                    }
                }
            } catch (Exception e) {
                if (ignoreInvalid) {
                    invalid++;
                } else {
                    CustomHeads.getInstance().getLogger().log(Level.WARNING, "Something went wrong while loading Category File " + file.getName(), e);
                    return;
                }
            }
        }

        if (!CustomHeads.hasReducedDebug())
            CustomHeads.getInstance().getServer().getConsoleSender().sendMessage(CustomHeads.chPrefix + "Successfully loaded " + loaded + " Categories from " + language + "/categories in " + (System.currentTimeMillis() - timestamp) + "ms " + (ignoreInvalid ? "(" + (ignored + invalid) + " " + ((ignored + invalid) == 1 ? "Category was" : "Categories were") + " ignored - " + ignored + " not loaded or not found, " + (invalid > 0 ? "§c" : "") + invalid + " Invalid§7)" : ""));
        CategoryLoader.loaded = true;
    }

    public int importSingle(File file) {
        JsonFile jsf = new JsonFile(file);
        try {
            Category category = Category.getConverter().fromJson(jsf.getJson(), Category.class);
            if (category == null) {
                return 3;
            } else {
                if (categories.containsKey(category.getId())) {
                    return 1;
                }
                categories.put(category.getId(), category);
                sourceFiles.put(category, file);
                if (category.hasSubCategories()) {
                    for (SubCategory subCategory : category.getSubCategories()) {
                        if (subCategories.containsKey(subCategory.getId())) {
                            return 2;
                        }
                        subCategories.put(subCategory.getId(), subCategory);
                    }
                }
                List<String> loadedCategories = CustomHeads.getCategoryLoaderConfig().get().isList("categories") ? CustomHeads.getCategoryLoaderConfig().get().getStringList("categories") : new ArrayList<>();
                loadedCategories.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                CustomHeads.getCategoryLoaderConfig().get().set("categories", loadedCategories);
                CustomHeads.getCategoryLoaderConfig().save();
            }
        } catch (Exception e) {
            CustomHeads.getInstance().getLogger().log(Level.WARNING, "Something went wrong while loading Category File " + file.getName(), e);
            return 4;
        }
        return 0;
    }

    /**
     * Removes an <code>Category</code> from the Loader Config
     *
     * @param category Category to be removed
     * @return Returns if Category was removed
     */
    public boolean removeCategory(Category category) {
        if (categories.containsKey(category.getId())) {
            categories.remove(category.getId());
            List<String> loadedCategories = CustomHeads.getCategoryLoaderConfig().get().isList("categories") ? CustomHeads.getCategoryLoaderConfig().get().getStringList("categories") : new ArrayList<>();
            loadedCategories.remove(CustomHeads.getCategoryLoader().getSourceFile(category).getName().substring(0, CustomHeads.getCategoryLoader().getSourceFile(category).getName().lastIndexOf(".")));
            CustomHeads.getCategoryLoaderConfig().get().set("categories", loadedCategories);
            CustomHeads.getCategoryLoaderConfig().save();
            return true;
        }
        return false;
    }

    /**
     * Permanently removes an <code>Category</code>
     *
     * @param category Category to be removed
     * @return Returns if Category was removed
     */
    public boolean deleteCategory(Category category) {
        return categories.containsKey(category.getId()) && getSourceFile(category).delete();
    }

    /**
     * Permanently removes an <code>SubCategory</code>
     *
     * @param subCategory SubCategory to be removed
     * @return Returns if Category was removed
     */
    public boolean deleteSubCategory(SubCategory subCategory) {
        Category originCategory = subCategory.getOriginCategory();
        if (subCategories.containsKey(subCategory.getId()) && originCategory != null) {
            List<SubCategory> subCategories = subCategory.getOriginCategory().getSubCategories();
            subCategories.remove(subCategory);
            originCategory.setSubCategories(subCategories);
            categories.put(originCategory.getId(), originCategory);

            // Rewrite Category File
            File categoryFile = getSourceFile(originCategory);
            try {
                FileOutputStream outputStream = new FileOutputStream(categoryFile);
                outputStream.write(originCategory.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public Category getCategory(String id) {
        return categories.get(id);
    }

    public SubCategory getSubCategory(String id) {
        return subCategories.get(id);
    }

    public List<Category> getCategoryList() {
        return new ArrayList<>(categories.values());
    }

    public List<SubCategory> getSubCategoryList() {
        return new ArrayList<>(subCategories.values());
    }

    public File getSourceFile(Category category) {
        return sourceFiles.get(category);
    }

    public List<BaseCategory> getAllCategories() {
        List<BaseCategory> categories = new ArrayList<>(getCategoryList());
        categories.addAll(getSubCategoryList());
        return categories;
    }

    public List<ItemStack> getAllHeads() {
        List<ItemStack> heads = new ArrayList<>();
        int index = 0;
        for (Category category : categories.values()) {
            if (category.hasSubCategories()) {
                for (SubCategory subCategory : category.getSubCategories()) {
                    for (ItemStack itemStack : subCategory.getHeads()) {
                        heads.add(CustomHeads.getTagEditor().addTags(new ItemEditor(itemStack).setLore(Arrays.asList("§7§o" + category.getId() + "-" + category.getName())).getItem(), "category", subCategory.getOriginCategory().getId(), "index", "" + (++index), "wearable"));
                    }
                }
            } else {
                for (ItemStack itemStack : category.getHeads()) {
                    heads.add(CustomHeads.getTagEditor().addTags(new ItemEditor(itemStack).setLore(Arrays.asList("§7§o" + category.getId() + "-" + category.getName())).getItem(), "category", category.getId(), "index", "" + (++index), "wearable"));
                }
            }
        }
        return heads;
    }

}
