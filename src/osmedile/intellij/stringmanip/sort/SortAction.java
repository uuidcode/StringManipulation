package osmedile.intellij.stringmanip.sort;

import java.util.*;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.util.TextRange;

import osmedile.intellij.stringmanip.utils.StringUtils;

public abstract class SortAction extends EditorAction {

	protected SortAction(Sort sort) {
		this(true, sort);
	}

	protected SortAction(boolean setupHandler, final Sort sort) {
		super(null);
		if (setupHandler) {
			this.setupHandler(new EditorWriteActionHandler(false) {

				@Override
				public void executeWriteAction(Editor editor, DataContext dataContext) {
					List<CaretState> caretsAndSelections = editor.getCaretModel().getCaretsAndSelections();

					if (caretsAndSelections.size() > 1) {
						processMultiCaret(editor, caretsAndSelections);
					} else if (caretsAndSelections.size() == 1) {
						processSingleSelection(editor, caretsAndSelections);
					}
				}

				private void processSingleSelection(Editor editor, List<CaretState> caretsAndSelections) {
					CaretState caretsAndSelection = caretsAndSelections.get(0);
					LogicalPosition selectionStart = caretsAndSelection.getSelectionStart();
					LogicalPosition selectionEnd = caretsAndSelection.getSelectionEnd();
					String text = editor.getDocument().getText(
							new TextRange(editor.logicalPositionToOffset(selectionStart),
									editor.logicalPositionToOffset(selectionEnd)));

					String charSequence = sort.sortLines(text);

					editor.getDocument().replaceString(editor.logicalPositionToOffset(selectionStart),
							editor.logicalPositionToOffset(selectionEnd), charSequence);
				}

				private void processMultiCaret(Editor editor, List<CaretState> caretsAndSelections) {
					List<String> lines = new ArrayList<String>();
					for (CaretState caretsAndSelection : caretsAndSelections) {
						LogicalPosition selectionStart = caretsAndSelection.getSelectionStart();
						LogicalPosition selectionEnd = caretsAndSelection.getSelectionEnd();
						String text = editor.getDocument().getText(
								new TextRange(editor.logicalPositionToOffset(selectionStart),
										editor.logicalPositionToOffset(selectionEnd)));
						lines.add(text);
					}

					lines = sort.sortLines(lines);

					for (int i = lines.size() - 1; i >= 0; i--) {
						String line = lines.get(i);
						CaretState caretsAndSelection = caretsAndSelections.get(i);
						LogicalPosition selectionStart = caretsAndSelection.getSelectionStart();
						LogicalPosition selectionEnd = caretsAndSelection.getSelectionEnd();
						editor.getDocument().replaceString(editor.logicalPositionToOffset(selectionStart),
								editor.logicalPositionToOffset(selectionEnd), line);
					}
				}
			});
		}
	}

	final static Comparator<String> COMPARATOR = new NaturalOrderComparator();
	// final static Comparator alphanumComparator = Ordering.natural();

	enum Sort {
		CASE_SENSITIVE_A_Z(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return SortAction.COMPARATOR.compare(o1, o2);
			}
		}),
		CASE_SENSITIVE_Z_A(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return SortAction.COMPARATOR.compare(o2, o1);
			}
		}),
		CASE_INSENSITIVE_A_Z(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return SortAction.COMPARATOR.compare(o1.toLowerCase(), o2.toLowerCase());
			}
		}),
		CASE_INSENSITIVE_Z_A(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return SortAction.COMPARATOR.compare(o2.toLowerCase(), o1.toLowerCase());
			}
		}),
		LINE_LENGTH_SHORT_LONG(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.length() - o2.length();
			}
		}),
		LINE_LENGTH_LONG_SHORT(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.length() - o1.length();

			}
		});

		private Comparator<String> comparator;

		Sort(Comparator<String> comparator) {
			this.comparator = comparator;
		}

		public List<String> sortLines(List<String> text) {
			Collections.sort(text, comparator);
			return text;

		}

		public String sortLines(String text) {
			String[] split = text.split("\n");

			List<String> list = Arrays.asList(split);
			sortLines(list);

			String join = StringUtils.join(split, '\n');
			if (text.endsWith("\n")) {
				join = join + "\n";
			}
			return join;
		}

		public Comparator<String> getComparator() {
			return comparator;
		}
	}
}
