package helpers;

import java.io.File;
import java.util.StringTokenizer;
import javax.swing.filechooser.FileFilter;

/**
 * 保存文件时候的拓展名管理，
 * 继承FileFilter，
 * 
 * @author zsx
 *
 */
public class ExtensionHelper extends FileFilter {

	/**
	 * 
	 * @param description 文件名
	 * @param extensions 拓展名
	 */
	public ExtensionHelper(String description, String[] extensions) {
		this.description = description;
		this.extensions = extensions;
	}

	/**
	 * 有多个拓展名的情况
	 * @param description
	 * @param extensions
	 */
	public ExtensionHelper(String description, String extensions) {
		this.description = description;
		//
		StringTokenizer tokenizer = new StringTokenizer(extensions, "|");
		this.extensions = new String[tokenizer.countTokens()];
		for (int i = 0; i < this.extensions.length; i++)
			this.extensions[i] = tokenizer.nextToken();
	}

	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		String fname = f.getName().toLowerCase();
		for (int i = 0; i < extensions.length; i++)
			if (fname.endsWith(extensions[i].toLowerCase()))
				return true;
		return false;
	}

	public String getDescription() {
		return description;
	}

	public String[] getExtensions() {
		return extensions;
	}

	private String description;
	private String[] extensions;
}
