interface Tab {
	id: string;
	label: string;
}

interface TabNavigationProps {
	tabs: Tab[];
	activeTab: string;
	onTabChange: (tabId: string) => void;
}

export const TabNavigation = ({
	tabs,
	activeTab,
	onTabChange,
}: TabNavigationProps) => {
	return (
		<div>
			<div className="sm:hidden">
				<select
					className="w-full rounded-md border-gray-200"
					value={activeTab}
					onChange={(e) => onTabChange(e.target.value)}
				>
					{tabs.map((tab) => (
						<option key={tab.id} value={tab.id}>
							{tab.label}
						</option>
					))}
				</select>
			</div>

			<div className="hidden sm:block">
				<div className="">
					<nav className="-mb-px flex gap-4">
						{tabs.map((tab) => (
							<button
								key={tab.id}
								onClick={() => onTabChange(tab.id)}
								className={`shrink-0 border-2 px-3 py-2 text-sm font-medium rounded-lg transition-all duration-150 hover:scale-105 ${
									activeTab === tab.id
										? " border-likeme-primary bg-likeme-primary text-white font-bold"
										: "border-likeme-secondary text-likeme-text hover:text-white hover:bg-likeme-primary"
								}`}
							>
								{tab.label}
							</button>
						))}
					</nav>
				</div>
			</div>
		</div>
	);
};
