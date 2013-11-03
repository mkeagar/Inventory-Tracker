/**
 * 
 */

package model.report;

/**
 * @author Michael
 * 
 */
public class RemovedItemsHTMLReportBuilder implements ReportBuilder
{
	// Variables
	private Visitor visitor = null;

	/**
	 * Constructor for RemovedItemsHTMLReportBuilder
	 * @pre visitor passed in must not be == null
	 * @post none
	 * @param visitor the visitor that will contain the data on the removed
	 *            items for this report
	 */
	public RemovedItemsHTMLReportBuilder(Visitor visitor)
	{
		this.visitor = visitor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.report.ReportBuilder#buildReport()
	 */
	@Override
	public void buildReport()
	{

	}

	/**
	 * Method to construct HTML header for this report. Called from
	 * buildReport().
	 * @pre this.visitor must not be == null
	 * @post Valid HTML string containing HTML header info is returned to
	 *       buildReport method
	 */
	private String buildHTMLHead()
	{

	}

	/**
	 * Method to construct HTML body for this report. Called from buildReport().
	 * @pre this.visitor must not be == null
	 * @post Valid HTML string containing HTML body info is returned to
	 *       buildReport method
	 */
	private String buildHTMLBody()
	{

	}

}
