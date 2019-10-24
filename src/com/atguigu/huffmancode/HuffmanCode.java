package com.atguigu.huffmancode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//霍夫曼编码解码
//此为字符串的编码解码
public class HuffmanCode {

	// 霍夫曼编码表
	private static Map<Byte, String> codeTable;

	static {

		// 初始化霍夫曼编码表
		codeTable = new HashMap<>();
	}

	public static void main(String[] args) {

		String str = "i like like like java do you like a java";

		byte[] arr = str.getBytes();

		// 将字符串编码
		byte[] zip = getHuffmanCode(arr);

		// 解码回原先的字符串
		byte[] bytes = getUnHuffmanCode(zip);

		String word = new String(bytes);

		System.out.println(word);
	}

	public static byte[] getUnHuffmanCode(byte[] zip) {

		// 将加密的数据 解开 变成经过编码的字符串
		String unZip = unZip(zip);

		// 将编码过的字符串还原为原字符串
		byte[] bytes = binaryStringToWord(unZip);

		return bytes;

	}

	private static byte[] binaryStringToWord(String unZip) {

		List<Byte> list = new ArrayList<>();

		while (unZip.length() > 0) {

			Set<Entry<Byte, String>> entrySet = codeTable.entrySet();

			for (Entry<Byte, String> entry : entrySet) {

				if (unZip.startsWith(entry.getValue())) {

					list.add(entry.getKey());
					unZip = unZip.substring(entry.getValue().length());

				}

			}

		}

		// TODO 感觉这个烂透了
		byte[] bytes = new byte[list.size()];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = list.get(i);
		}

		return bytes;

	}

	private static String unZip(byte[] zip) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < zip.length; i++) {

			if (i != zip.length - 1) {
				sb.append(byteToBinaryString(zip[i], true));
			} else {
				sb.append(byteToBinaryString(zip[i], false));
			}

		}

		return sb.toString();

	}

	private static String byteToBinaryString(byte b, boolean flag) {

		int temp = b;

		if (flag) {

			temp = temp | 256;

		}

		String binary = Integer.toBinaryString(temp);

		if (flag) {
			return binary.substring(binary.length() - 8);
		} else {
			return binary;
		}

	}

	// 整合 霍夫曼编码方法
	public static byte[] getHuffmanCode(byte[] arr) {

		// 统计字节数组中元素及出现的次数保存到node中
		List<Node> nodes = getNodeTimeList(arr);

		// 生成霍夫曼树并返回根节点
		Node root = createHuffmanTree(nodes);

		// 生成霍夫曼编码表
		createHuffmanCode(root, "");

		// 按照霍夫曼编码表 将数据加密
		String encryptedContent = encryption(arr);

		// 将加密内容 转成byte数组形式发送
		byte[] result = zip(encryptedContent);

		return result;

	}

	// 将加密内容 按照8位1个的形式转成byte数组
	// 为啥是8位？ 因为byte在二进制中存储的最大值为1111 1111
	private static byte[] zip(String encryptedContent) {

		StringBuffer sb = new StringBuffer(encryptedContent);

		byte[] arr = null;

		// 初始化数组长度
		if (encryptedContent.length() % 8 == 0) {
			arr = new byte[encryptedContent.length() / 8];
		} else {
			arr = new byte[encryptedContent.length() / 8 + 1];
		}

		int index = 0;

		while (!"".equals(sb.toString())) {

			String temp;

			if (sb.length() >= 8) {
				temp = sb.substring(0, 8);

				sb.delete(0, 8);
			} else {
				temp = sb.toString();

				sb.delete(0, sb.length());
			}

			arr[index++] = (byte) (Integer.parseInt(temp, 2));

		}

		return arr;

	}

	// 将内容加密
	private static String encryption(byte[] arr) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < arr.length; i++) {

			String word = codeTable.get(arr[i]);

			sb.append(word);

		}

		return sb.toString();

	}

	// 递归 生成霍夫曼编码表
	// 霍夫曼编码默认认为 如果分支往左走 则 编码+"0" 往右走则+"1"
	private static void createHuffmanCode(Node root, String code) {

		// 一旦根节点都是空的。。 避免下面空指针
		if (root == null) {
			return;
		}

		// 只保存叶子节点的数据
		// 具体原因请看 霍夫曼树的生成
		if (root.getLeft() == null && root.getRight() == null) {
			codeTable.put(root.getName(), code);
			return;
		}

		// 往左走
		if (root.getLeft() != null) {
			createHuffmanCode(root.getLeft(), code + 0);
		}

		// 往右走
		if (root.getRight() != null) {
			createHuffmanCode(root.getRight(), code + 1);
		}

	}

	/**
	 * 生成霍夫曼树
	 * 
	 * @param nodes
	 * @return
	 */
	private static Node createHuffmanTree(List<Node> nodes) {

		while (nodes.size() > 1) {

			// 先按照出现次数从小到大排序
			Collections.sort(nodes);

			// 取出最小的和第二小的
			Node left = nodes.remove(0);
			Node right = nodes.remove(0);

			// 组成一个二叉树
			Node root = new Node((byte) 0, left.getTimes() + right.getTimes());
			root.setLeft(left);
			root.setRight(right);

			// 添加进集合中
			nodes.add(root);

		}

		return nodes.get(0);

	}

	/**
	 * 将字节码数组中的元素存入list以备生成霍夫曼树
	 * 
	 * @param arr
	 *            文件的字节数组
	 * @return
	 */
	private static List<Node> getNodeTimeList(byte[] arr) {

		List<Node> list = new ArrayList<>();

		Map<Byte, Integer> map = new HashMap<>();

		for (int i = 0; i < arr.length; i++) {

			Integer count = map.get(arr[i]);

			if (count == null) {
				map.put(arr[i], 1);
			} else {
				map.put(arr[i], count + 1);
			}

		}

		Set<Entry<Byte, Integer>> entrySet = map.entrySet();

		for (Entry<Byte, Integer> entry : entrySet) {
			list.add(new Node(entry.getKey(), entry.getValue()));
		}

		return list;

	}

}

// 霍夫曼树的节点
class Node implements Comparable<Node> {

	private byte name;

	private int times;

	private Node left;

	private Node right;

	public Node() {
		super();
	}

	public Node(byte name, int times) {
		super();
		this.name = name;
		this.times = times;
	}

	public byte getName() {
		return name;
	}

	public void setName(byte name) {
		this.name = name;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return "Node [name=" + name + ", times=" + times + "]";
	}

	@Override
	public int compareTo(Node o) {
		return this.times - o.getTimes();
	}

}